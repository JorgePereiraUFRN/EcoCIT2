package br.com.ecodif.service;

import java.util.List;

import javax.inject.Inject;

import br.com.ecodif.dao.ConnectedDeviceDAO;
import br.com.ecodif.dao.DeviceDAO;
import br.com.ecodif.dao.PlatformDAO;
import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.domain.Device;
import br.com.ecodif.domain.Platform;

public class DeviceService {
	
	@Inject
	private DeviceDAO deviceDAO = new DeviceDAO();

	@Inject
	private PlatformDAO platformDAO = new PlatformDAO();
	
	@Inject
	private ConnectedDeviceDAO connectedDeviceDAO = new ConnectedDeviceDAO();
	
	public List<Device> findAllDevices() {
		return deviceDAO.findAll();
	}
	
	public void saveDevice(Device device){
		deviceDAO.save(device);
	}
	
	public Platform findPlatformWithDevices(int platform_id) {
		return platformDAO.findPlatformWithDevices(platform_id);
	}
	
	public void updatePlatform(Platform platform) {
		platformDAO.update(platform);
	}

	public List<Device> findDevicesByUser(String login) {
		return deviceDAO.findDevicesByUser(login);
	}
	
	public Device findDevice(int id){
		return deviceDAO.find(id);
	}
	
	public void deleteDevice(Device device){
		deviceDAO.delete(device);
	}
	
	public List<ConnectedDevice> findConnectedDevicesByUser(String login) {
		return connectedDeviceDAO.findConnectedDevicesByUser(login);
	}
	
	public ConnectedDevice findConnectedDeviceById(int id) {
		return connectedDeviceDAO.find(id);
	}
	
	public boolean connectedDeviceHasEnvironment(int id){
		return connectedDeviceDAO.hasEnvironments(id);
	}
	
	public void deleteConnectedDevice(ConnectedDevice cnnDevice) {
		connectedDeviceDAO.delete(cnnDevice);
	}
	
	public void saveCnnDevice(ConnectedDevice cnnDevice){
		connectedDeviceDAO.save(cnnDevice);
	}
	
	public Device findDeviceByEnvironmentId(int id) {
		return deviceDAO.findDeviceByEnvironmentId(id);				
	}
	
	public ConnectedDevice findConnectedDeviceWithEnvironments(int id) {
		return connectedDeviceDAO.findConnectedDeviceWithEnvironments(id);
	}
	
	public void updateConnectedDevice(ConnectedDevice cnnDevice) {
		connectedDeviceDAO.update(cnnDevice);
	}
}
