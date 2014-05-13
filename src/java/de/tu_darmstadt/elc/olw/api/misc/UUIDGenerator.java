package de.tu_darmstadt.elc.olw.api.misc;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;

public class UUIDGenerator {
	private long currentTimeMillis = 0L;
	private long generatedUuidsThisMillisecond = 0;
    private short clockSequence = 0;	
	private byte[] hardwareAddress = null;	
	
	public UUIDGenerator() {
        NetworkInterface networkInterface = null;
        Enumeration<NetworkInterface> enumeration = null;
        
        try {
        	// Prepare clock sequence
        	this.clockSequence = (short)new Random().nextInt(0x4000);
        	
        	// Get hardware address of physical network adapter
        	enumeration = NetworkInterface.getNetworkInterfaces();
        	while(enumeration.hasMoreElements()) {
            	networkInterface = enumeration.nextElement();
            	if ((!networkInterface.isLoopback()) && (!networkInterface.isPointToPoint()) && (!networkInterface.isVirtual())) {
        	        this.hardwareAddress = networkInterface.getHardwareAddress();
        	        break;
            	}
        	}
        } catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	// ff-61-05-88-ab-ab-11-df-8b-d5-00-1f-3c-a7-fc-66
	// ff610588-abab-11df-8bd5-001f3ca7fc66
    public UUID generate() {
        long time = 0;
        long most = 0;
        long least = 0;
        UUID result = null;         
           
        // Check whether a new millisecond has started since the previous call
        if (this.currentTimeMillis != System.currentTimeMillis()) {
        	this.currentTimeMillis = System.currentTimeMillis();
        	this.generatedUuidsThisMillisecond = 0;
        }

        // Translate current time millis plus offset, times 100 ns ticks
        time = (this.currentTimeMillis + 12219292800000L) * 10000L;

        // Check whether UUID generation is triggered too fast (> 10000 UUIDs per ms)
        if (this.generatedUuidsThisMillisecond + 1 >= 10000L) {
            throw new RuntimeException();
        }

        // Adjust time to handle up to 10000 generated uuids per millisecond
        time += this.generatedUuidsThisMillisecond;
        this.generatedUuidsThisMillisecond++;
                
        // Set most significant long
        most = 0;
        most |= ((time >> 24) & 0xFF) << 56;
        most |= ((time >> 16) & 0xFF) << 48;
        most |= ((time >> 8) & 0xFF) << 40;
        most |= (time & 0xFF) << 32;
        most |= ((time >> 40) & 0xFF) << 24;
        most |= ((time >> 32) & 0xFF) << 16;        
        most |= 0x10 << 8;
        most |= ((time >> 56) & 0x0F) << 8;
        most |= ((time >> 48) & 0xFF);
        
        // Set least significant long
        least = 0;
        least |= ((long)0x80 << 56);
        least |= (((long)this.clockSequence & 0x3F00) >> 8) << 56;
        least |= ((long)this.clockSequence & 0xFF) << 48;
        least |= (((long)this.hardwareAddress[0] & 0xFFL) << 40);
        least |= (((long)this.hardwareAddress[1] & 0xFFL) << 32);
        least |= (((long)this.hardwareAddress[2] & 0xFFL) << 24);
        least |= (((long)this.hardwareAddress[3] & 0xFFL) << 16);
        least |= (((long)this.hardwareAddress[4] & 0xFFL) << 8);
        least |= ((long)this.hardwareAddress[5] & 0xFFL);

        // Generate UUID object
        result = new UUID(most, least);
        
        // Return result
        return result;
    }
    
    
    public String generateUUIDString() {
    	return this.generate().toString();
    }
    
    public String generateUUIDDirHiearachy() {
    	UUID uuid = this.generate();
    	
    	return UUIDGenerator.splitUUID(uuid.toString());
    }
    
    public static String getParentUUID(String uuid) {
    	String[] tokens = uuid.split("-");
    	String parentUUID="";
    	for (int i = 0; i < tokens.length - 2; i++)
    		parentUUID = parentUUID + tokens[i] + "-";
    	if (tokens.length >=2)
    		parentUUID += tokens[tokens.length - 2];
    	return parentUUID;
    }
    
    public static String getPathFromUUID(String uuid) {
    	return UUIDGenerator.splitUUID(uuid).replace("-", "/");
    }
    
    public static String splitUUID(String originalUUID) {
    	 //for test
       	if (originalUUID.length() == 2)
    		return originalUUID;
    	String uuidChain = "";
    	
    	char[] uuidArray = originalUUID.toString().replaceAll("-", "").toCharArray();
    	
    	for (int i = 0; i < uuidArray.length -1; i ++) {
    		uuidChain += uuidArray[i];
    		if (i % 2 != 0)
    			uuidChain += "-";
    	}
    	uuidChain += uuidArray[uuidArray.length -1];
    	if (uuidArray.length % 2 != 0)
    		uuidChain += "0";
    	return uuidChain;
    	
    }
    
}
