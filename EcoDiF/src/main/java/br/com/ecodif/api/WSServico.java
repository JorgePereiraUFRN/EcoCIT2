package br.com.ecodif.api;

import java.util.HashSet;
import java.util.Set;
 
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
 
@ApplicationPath("/api")
public class WSServico extends Application {
 
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
 
	public WSServico() {
		singletons.add(new ApplicationResource());
		singletons.add(new EemlResource());
		singletons.add(new ConnectedDeviceResource());
		singletons.add(new DeviceResource());
		singletons.add(new EnvironmentResource());
		singletons.add(new PlatformResource());
		singletons.add(new SensorResource());
		singletons.add(new UnitResource());
		singletons.add(new UserResource());
		
	}
 
	public Set<Class<?>> getClasses() {
		return empty;
	}
 
	public Set<Object> getSingletons() {
		return singletons;
	}
}