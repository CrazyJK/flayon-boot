package jk.kamoru.flayon.boot;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jk.kamoru.flayon.boot.error.FlayOnException;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/manager")
public class ManagerController {

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
		return "manager/requestMappingList";
	}
	
	@RequestMapping("/error")
	public String error(Model model, @RequestParam(value="k", required=false, defaultValue="") String kind) {
		if (kind.equals("default"))
			throw new RuntimeException("default error");
		else if (kind.equals("falyon"))
			throw new FlayOnException("falyon error", new Exception("flayon error"));
		
		return "manager/occurError";
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
		
		return "manager/memory";
	}
	
	@RequestMapping("/colors")
	public String colors() {
		return "manager/colors";
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
