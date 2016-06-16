package jk.kamoru.flayon.boot;

import java.io.File;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
			mappingData.put("reqPattern", Utils.substringBetween(entry.getKey().getPatternsCondition().toString(), "[", "]"));
			mappingData.put("reqMethod",  Utils.substringBetween(entry.getKey().getMethodsCondition().toString(), "[", "]"));
			mappingData.put("beanType",   Utils.substringAfterLast(entry.getValue().getBeanType().getName(), "."));
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
		if (Utils.isEmpty(ip))
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
		model.addAttribute("portArr", Utils.join(portArr, ","));
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
			if (!Utils.isEmpty(info.getName())) {
				if (!threadInfo.getThreadName().startsWith(info.getName())) continue;
			}
			
			// filter state
			if (!Utils.isEmpty(info.getState())) {
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

	@RequestMapping("/logviewt")
	public String logView(HttpServletRequest request, Model model) {
		
		String logpath   = request.getParameter("logpath");
		String delimeter = request.getParameter("delimeter");
		String search    = request.getParameter("search");
		String searchOper= request.getParameter("searchOper");

		logpath   = logpath   == null ? "" : logpath.trim();
		delimeter = delimeter == null ? "" : delimeter;
		search    = search    == null ? "" : search.trim();
		searchOper= searchOper== null ? "or" : searchOper;

		List<String[]> lines = new ArrayList<String[]>();
		int tdCount = 0;
		String msg = "";
		try {
			lines = Utils.readLines(logpath, delimeter, search, searchOper);
			for (String[] line : lines) {
				tdCount = line.length > tdCount ? line.length : tdCount;
			}
		}
		catch (Exception e) {
			msg = e.getMessage();
		}
		
		model.addAttribute("lines", lines);
		model.addAttribute("tdCount", tdCount);
		model.addAttribute("msg", msg);
		model.addAttribute("logpath", logpath);
		model.addAttribute("delimeter", delimeter);
		model.addAttribute("search", search);
		model.addAttribute("searchOper", searchOper);

		return "flayon/logView";
	}

}

@Data
class ThreadParamInfo {
	
	private String name;
	private String state;
	private long threadId;

	public boolean hasAnyValue() {
		return StringUtils.hasLength(name) || StringUtils.hasLength(state) || threadId > 0;
	}
}

class Utils {

	static List<String[]> readLines(String logpath, String delimeter, String search, String searchOper) throws Exception {
		if (logpath.length() == 0)
			throw new Exception("log path is empty");
		File file = new File(logpath);
		if (file.isDirectory()) 
			throw new Exception("log path is directory");
		if (!file.exists())
			throw new Exception("log file not exist");
		
		List<String[]> lineArrayList = new ArrayList<String[]>();
		String[] searchArray = trimArray(splitByWholeSeparatorWorker(search, ",", -1, false));
		int count = 0;
		System.out.println("logView readLines Start");
		for (String line : Files.readAllLines(file.toPath())) {
			if (searchArray.length == 0 || containsAny(line, searchArray, searchOper)) {
				line = replaceEach(line, new String[]{"<", ">"}, new String[]{"&lt;", "&gt;"}, false, 0);
				line = replaceEach(line, searchArray, wrapString(searchArray, "<em>", "</em>"), false, 0);
				lineArrayList.add(delimeter.length() > 0 ? splitByWholeSeparatorWorker(line, delimeter, -1, false) : new String[]{line});
				if (++count % 100 == 0)
					System.out.println("logView readLines " + count);
			}
		}
		System.out.println("logView readLines End");
		return lineArrayList;
	}
	
	static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

        if (text == null || text.length() == 0 || searchList == null ||
                searchList.length == 0 || replacementList == null || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - " +
                                            "output of one loop is the input of another");
        }

        int searchLength = searchList.length;
        int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: "
                + searchLength
                + " vs "
                + replacementLength);
        }

        // keep track of which still have matches
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                    searchList[i].length() == 0 || replacementList[i] == null) {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                        searchList[i].length() == 0 || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }
	
	static String[] wrapString(String[] strArr, String str1, String str2) {
		String[] retArr = Arrays.copyOf(strArr, strArr.length);
		for (int i=0; i < strArr.length; i++) {
			retArr[i] = str1 + strArr[i] + str2;
		}
		return retArr;
	}
	
	static boolean containsAny(String str, String[] searchArr, String searchOper) {
		boolean result = true;
		for (String search : searchArr) {
			if (searchOper.equals("or")) {
				return contains(str, search);
			}
			else if (searchOper.equals("and")) {
				result = result && contains(str, search);
			}
		}
		return result;
	}
	static boolean contains(String str, String search) {
		if (str == null || str == null) return false;
		return str.contains(search);
	}

	static String[] trimArray(String[] strArr) {
		for (int i=0; i < strArr.length; i++) {
			strArr[i] = strArr[i].trim();
		}
		return strArr;
	}
	
	static String join(Object[] array, String separator) {
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
	
	static String substringBetween(String str, String open, String close) {
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
	
	static String substringAfterLast(String str, String separator) {
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
	
	static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
	
	static String[] splitByWholeSeparatorWorker(
            String str, String separator, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }

        int len = str.length();

        if (len == 0) {
            return new String[0];
        }

        if (separator == null || "".equals(separator)) {
            // Split on whitespace.
            return splitWorker(str, null, max, preserveAllTokens);
        }

        int separatorLength = separator.length();

        ArrayList<String> substrings = new ArrayList<String>();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);

            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings += 1;

                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        // The following is OK, because String.substring( beg, end ) excludes
                        // the character at the position 'end'.
                        substrings.add(str.substring(beg, end));

                        // Set the starting point for the next search.
                        // The following is equivalent to beg = end + (separatorLength - 1) + 1,
                        // which is the right calculation:
                        beg = end + separatorLength;
                    }
                } else {
                    // We found a consecutive occurrence of the separator, so skip it.
                    if (preserveAllTokens) {
                        numberOfSubstrings += 1;
                        if (numberOfSubstrings == max) {
                            end = len;
                            substrings.add(str.substring(beg));
                        } else {
                            substrings.add("");
                        }
                    }
                    beg = end + separatorLength;
                }
            } else {
                // String.substring( beg ) goes from 'beg' to the end of the String.
                substrings.add(str.substring(beg));
                end = len;
            }
        }

        return substrings.toArray(new String[substrings.size()]);
    }
	static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }
	
}
