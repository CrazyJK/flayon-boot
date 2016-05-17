package jk.kamoru.flayon.boot;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jk.kamoru.flayon.boot.error.FlayOnException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/flayon")
public class FlayOnController {

	@Autowired
	private ApplicationContext context;
	
	@RequestMapping(value = "/requestMappingList")
	public String requestMapping(Model model, @RequestParam(value="sort", required=false, defaultValue="P") final String sort) {
		log.debug("Request mapping list");

		RequestMappingHandlerMapping rmhm = context.getBean(RequestMappingHandlerMapping.class);

		List<Map<String, String>> mappingList = new ArrayList<Map<String, String>>();
		
		for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : rmhm.getHandlerMethods().entrySet()) {
			Map<String, String> mappingData = new HashMap<String, String>();
			mappingData.put("reqPattern", substringBetween(entry.getKey().getPatternsCondition().toString(), "[", "]"));
			mappingData.put("reqMethod",  substringBetween(entry.getKey().getMethodsCondition().toString(), "[", "]"));
			mappingData.put("beanType",   substringAfterLast(entry.getValue().getBeanType().getName(), "."));
			mappingData.put("beanMethod", entry.getValue().getMethod().getName());

			mappingList.add(mappingData);
		}
		Collections.sort(mappingList, new Comparator<Map<String, String>>() {
			@Override
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				if (sort.equals("P")) {
					return o1.get("reqPattern").compareTo(o2.get("reqPattern"));
				}
				else if (sort.equals("M")) {
					return o1.get("reqMethod").compareTo(o2.get("reqMethod"));
				}
				else if (sort.equals("C")) {
					int firstCompare = o1.get("beanType").compareTo(o2.get("beanType"));
					int secondCompare = o1.get("beanMethod").compareTo(o2.get("beanMethod"));
					return firstCompare == 0 ? secondCompare : firstCompare;
				}
				else {
					return o1.get("reqPattern").compareTo(o2.get("reqPattern"));
				}
			}
		});
		model.addAttribute("mappingList", mappingList);
		return "flayon/requestMappingList";
	}
	
	@RequestMapping("/error")
	public String error(Model model, @RequestParam(value="k", required=false, defaultValue="") String kind) {
		if (kind.equals("default"))
			throw new RuntimeException("default error");
		else if (kind.equals("falyon"))
			throw new FlayOnException("falyon error", new Exception("flayon error"));
		
		return "flayon/occurError";
	}

	@RequestMapping("/memory")
	public String memoryInfo(Model model) {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
		MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
		double heapUsedPercent = ((double)heapMemoryUsage.getUsed()/(double)heapMemoryUsage.getMax()) * 100.0D;
		double nonHeapUsedPercent = nonHeapMemoryUsage.getMax() == -1 ? 0 :
				((double)nonHeapMemoryUsage.getUsed()/(double)nonHeapMemoryUsage.getMax()) * 100.0D;

		model.addAttribute("heap", heapMemoryUsage);
		model.addAttribute("nonHeap", nonHeapMemoryUsage);
		model.addAttribute("heapUsedPercent", heapUsedPercent);
		model.addAttribute("nonHeapUsedPercent", nonHeapUsedPercent);
		
		return "flayon/memory";
	}
	
	@RequestMapping("/colors")
	public String colors() {
		return "flayon/colors";
	}
	
	@RequestMapping("/portscan")
	public String portscan(HttpServletRequest request, Model model) {
		
		String ip = request.getParameter("ip");
		if (isEmpty(ip))
			ip = "127.0.0.1";
		int port_s = 0;
		try {
			port_s = Integer.parseInt(request.getParameter("ports"));
		} catch (Exception e) {}
		int port_e = 0;
		try {
			port_e = Integer.parseInt(request.getParameter("porte"));
		} catch (Exception e) {}
		String[] portArr = null;
		try {
			portArr = request.getParameter("portArr").split(",");
		} catch (Exception e) {}

		List<Integer> ports = new ArrayList<>(); 
		if (port_s > 0 && port_s < port_e)
			for(int port=port_s; port<= port_e; port++)
				ports.add(port);
		else if (portArr != null)
			for(String port : portArr)
				if (port.trim().length() > 0)
					try {
						int _port = Integer.parseInt(port.trim());
						if (_port > 0)
							ports.add(_port);
					} catch (Exception e) {}

		List<Object[]> results = new ArrayList<>();
		if (ports.size() > 0) {
			final int timeout = 200;
			for(int port : ports) {
				try {
					Socket socket = new Socket();
		          	socket.connect(new InetSocketAddress(ip, port), timeout);
		          	socket.close();
		          	results.add(new Object[] {ip, port, true});
		        } catch (Exception ex) {
					results.add(new Object[] {ip, port, false});
		        }
			}
		}
		model.addAttribute("results", results);
		model.addAttribute("ip", ip);
		model.addAttribute("ports", port_s);
		model.addAttribute("porte", port_e);
		model.addAttribute("portArr", join(portArr, ","));
		return "flayon/portscan";
	}
	
	@RequestMapping("/webcontext")
	public String webContext() {
		return "flayon/webContext";
	}
	
	@RequestMapping("/threaddump")
	public String threadDump(ThreadParamInfo info, Model model) {
				
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfoArray = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
		List<ThreadInfo> threadInfos = new ArrayList<>();
		for (ThreadInfo threadInfo : threadInfoArray) {
		
			// filter the current thread
			if (threadInfo.getThreadId() == Thread.currentThread().getId()) continue;
			
			// filter name
			if (!isEmpty(info.getName())) {
				if (!threadInfo.getThreadName().startsWith(info.getName())) continue;
			}
			
			// filter state
			if (!isEmpty(info.getState())) {
				if (!threadInfo.getThreadState().toString().equals(info.getState())) continue;
			}
			
			// show only specific thread id
			if (info.getThreadId() > 0) {
				if (threadInfo.getThreadId() != info.getThreadId()) continue;
			}
			
			threadInfos.add(threadInfo);
		}
		log.info("========== threadInfos size {}", threadInfos.size());
		Collections.sort(threadInfos, new Comparator<ThreadInfo>() {

			@Override
				public int compare(ThreadInfo o1, ThreadInfo o2) {
					if (o1 == null || o2 == null) return 0;
					return o1.getThreadName().compareTo(o2.getThreadName());
				}
				
			}
		);
		List<String> states = new ArrayList<>();
		for (State _state : Thread.State.values()) {
			states.add(_state.toString());
		}
		
		model.addAttribute("threadInfos", threadInfos);
//		model.addAttribute("threadStates", states);
		model.addAttribute("threadStates", Thread.State.values());
		model.addAttribute("paramInfo", info);
		
		return "flayon/threadDump";
	}
		
	String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }

        int startIndex = 0;
        int endIndex = array.length;
        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }

        StringBuilder buf = new StringBuilder(noOfItems * 16);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
	}
	
	String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
	
	String substringAfterLast(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return "";
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == str.length() - separator.length()) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
	
	boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}

@Data
class ThreadParamInfo {
	
	private String name;
	private String state;
	private long threadId;

}

