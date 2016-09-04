package jk.kamoru.flayon.boot.aop;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class AccessLog {
    @Id
    public String id;
    public Date accessDate;
    public String remoteAddr;
    public String method;
    public String requestURI;
    public String contentType;
    public long elapsedTime;
    public String handlerInfo;
    public String exceptionInfo;
    public String modelAndViewInfo;
    
	public AccessLog(Date accessDate, String remoteAddr, String method, String requestURI, String contentType, long elapsedTime,
			String handlerInfo, String exceptionInfo, String modelAndViewInfo) {
		super();
		this.accessDate = accessDate;
		this.remoteAddr = remoteAddr;
		this.method = method;
		this.requestURI = requestURI;
		this.contentType = contentType;
		this.elapsedTime = elapsedTime;
		this.handlerInfo = handlerInfo;
		this.exceptionInfo = exceptionInfo;
		this.modelAndViewInfo = modelAndViewInfo;
	}
    
    
}
