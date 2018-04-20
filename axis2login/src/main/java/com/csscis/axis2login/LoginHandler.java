package com.csscis.axis2login;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.transport.http.HTTPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler extends AbstractHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());  
	private static String security="security";
	private static String username="username";
	private static String password="password";
	private static String role="role";
	private static Map<String,String> users;
	public void setConf(){
		Properties pro = new Properties();
		if(users==null){
			try {
				InputStream is=LoginHandler.class.getResourceAsStream("/axis2/axis2-user.properties");
				pro.load(is);
				users=new HashMap<String,String>();
				for(String key:pro.stringPropertyNames()){
					users.put(key, pro.getProperty(key));
				}
				is.close();
			} catch (IOException e) {
				new AxisFault("axis1-user.properties未设置用户名/密码!");
			}
		}
		return;
	}
	public OMElement find(Iterator<OMElement> el,String name){
		while (el.hasNext()) {
			OMElement s = el.next();
			if(s.getLocalName().equals(name)){
				return s;
			}else{
				return find(s.getChildElements(),name);
			}
		}
		return null;
	}
	public boolean validateNameSpaceUrl(MessageContext msgContext,String role){
		
		String sd=msgContext.getAxisService().getSchemaTargetNamespace();
		if(sd.indexOf(role)>0){
			return true;
		}
		return false;
	}
	public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
		setConf();
		if(logger.isDebugEnabled()){
			logger.info("getRootContext:"+msgContext.getServiceContext().getRootContext().toString());
			logger.info("getEnvelope:"+msgContext.getEnvelope().toString());
			HttpServletRequest request = (HttpServletRequest) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);  
	        String ip= request.getRemoteAddr();
	        logger.info("getRemoteAddr:"+ip);
		}
		Iterator<OMElement> list = (Iterator<OMElement>) msgContext.getEnvelope().getHeader().getChildElements();
		//得到security
		OMElement sec=find(list,security);
		if(sec==null)return InvocationResponse.ABORT;
		//list = sec.getChildren();会导致类型安全问题：java.lang.ClassCastException: org.apache.axiom.om.impl.llom.CharacterDataImpl cannot be cast to org.apache.axiom.om.OMElement
		list=sec.getChildElements();
	
		String u = "";
		String r="";
		String p = "";
		String dbu="";
		String dbp="";
		while (list.hasNext()) {
			OMElement element = (OMElement) list.next();
			if (element.getLocalName().equals(username)) {
				u = element.getText();
			}
			if (element.getLocalName().equals(password)) {
				p = element.getText();
			}
			if (element.getLocalName().equals(role)) {
				r = element.getText();
				dbu=users.get(r+"."+username);
				dbp=users.get(r+"."+password);
			}
		}
		//System.out.println(u+p+":"+dbu+dbp);
		if(isEmpty(r) || isEmpty(u) || isEmpty(p)){
			logger.info("用户名、密码、角色为空.");
			return InvocationResponse.ABORT;
		}
		if(!validateNameSpaceUrl(msgContext,r)){
			logger.info("角色："+r+"未授权.");
			return InvocationResponse.ABORT;
		}
		if(u.equals(dbu) && p.equals(dbp)){
			return InvocationResponse.CONTINUE;
		}
		logger.info("角色"+r+",帐号"+u+",密码"+p+",未授权.");
		return InvocationResponse.ABORT;
	}
	private boolean isEmpty(String sd){
		if(sd==null || sd.equals("") || sd.trim().equals("")){
			return true;
		}
		return false;
	}
}
