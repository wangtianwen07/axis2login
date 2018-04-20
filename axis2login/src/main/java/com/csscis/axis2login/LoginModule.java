package com.csscis.axis2login;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;

public class LoginModule implements org.apache.axis2.modules.Module{

	public void init(ConfigurationContext configContext, AxisModule module) throws AxisFault {
		// TODO Auto-generated method stub
		
	}

	public void engageNotify(AxisDescription axisDescription) throws AxisFault {
		// TODO Auto-generated method stub
		
	}

	public boolean canSupportAssertion(Assertion assertion) {
		// TODO Auto-generated method stub
		return false;
	}

	public void applyPolicy(Policy policy, AxisDescription axisDescription) throws AxisFault {
		// TODO Auto-generated method stub
		
	}

	public void shutdown(ConfigurationContext configurationContext) throws AxisFault {
		// TODO Auto-generated method stub
		
	}

}
