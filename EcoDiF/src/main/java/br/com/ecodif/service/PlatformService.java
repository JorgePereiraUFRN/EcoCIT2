package br.com.ecodif.service;

import javax.inject.Inject;

import br.com.ecodif.dao.PlatformDAO;
import br.com.ecodif.domain.Platform;


public class PlatformService {


    @Inject
    private PlatformDAO platformDAO = new PlatformDAO();

    public void savePlatform(Platform platform) {
        platformDAO.save(platform);
    }

    public Platform findDevicesWithPlatform(int platform_id) {
        return  platformDAO.findPlatformWithDevices(platform_id);
    }

    public Platform findPlatformByDevice(int device_id) {
        return platformDAO.findPlatformByDevice(device_id);
    }
    
    public Platform findById(int platform_id) {
    	return platformDAO.find(platform_id);
    }
}