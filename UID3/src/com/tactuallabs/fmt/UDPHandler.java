package com.tactuallabs.fmt;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class UDPHandler implements Runnable{

	private IFMTEventHandler m_FMTEventHandler;
	private FMTFrame m_CurrentFrame = new FMTFrame();
	
	public UDPHandler(IFMTEventHandler inFMTCheckerboardFrame) {
		m_FMTEventHandler = inFMTCheckerboardFrame;
	}

	private int IntFromByteArray(byte[] data, int offset) {
		
//		int ret = 0;
//		  for (int i=3; i>=0 && i+offset<data.length; i--) {
//		    ret <<= 8;
//		    ret |= (int)data[i+offset] & 0xFF;
//		  }
//		  return ret;
		
//			  long ret = 0;
//			  for (int i=0; i<4 && i+offset<data.length; i++) {
//			    ret <<= 8;
//			    ret |= (long)data[i+offset] & 0xFF;
//			  }
//			  return (int)ret;
//			
			
		//System.out.println(data[offset] + ":" + data[offset+1] + ":" + data[offset+2] + ":" + data[offset+3] );
		int ret = 0;
		ret += (data[offset]) & 0xFF;
		ret += ((data[offset+1]) & 0xFF) << 8;
		ret += ((data[offset+2]) & 0xFF) << 16;
		ret += ((data[offset+3]) & 0xFF) << 24;
		return ret; 
	}
	
private int get2ByteInt(byte[] data, int offset) {
		
		int ret = 0;
		ret += (data[offset]) & 0xFF;
		ret += ((data[offset+1]) & 0xFF) << 8;
		return ret; 
	}

	@Override
	public void run() {
		try{
			List<InetAddress> addrList = new ArrayList<InetAddress>();
			Enumeration<NetworkInterface> enumer = NetworkInterface
					.getNetworkInterfaces();
			while (enumer.hasMoreElements()) {
				NetworkInterface ifc = enumer.nextElement();
				if (ifc.isUp()) {
					Enumeration<InetAddress> enumer2 = ifc.getInetAddresses();
					while (enumer2.hasMoreElements()) {
						InetAddress addr = enumer2.nextElement();
						addrList.add(addr);
					}
				}
			}
			
			for(InetAddress inetaddr:addrList){
				System.out.println(inetaddr);
			}
			
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			try {
			      int port = 60001;

			      // Create a socket to listen on the port.
			      DatagramSocket dsocket = new DatagramSocket(port);

			      // Create a buffer to read datagrams into. If a
			      // packet is larger than this buffer, the
			      // excess will simply be discarded!
			      byte[] buffer = new byte[2048];

			      // Create a packet to receive data into the buffer
			      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			      // Now loop forever, waiting to receive packets and printing them.
			      while (true) {
			        // Wait to receive a datagram
			        dsocket.receive(packet);

			        
			        
			        // Convert the contents to a string, and display them
			        String msg = new String(buffer, 0, packet.getLength());
			        
			        int N = (int)packet.getData()[0];
			        int B = (int)packet.getData()[1];
			        //System.out.println(N + " = " + B);
			        if(N == -86){
			        	// TODO send the touch data as well!
			        	
			        	for(int touchID=0;touchID<64;touchID++){
			        		
			        		int xPt = get2ByteInt(packet.getData(),13 + touchID*22);
			        		int yPt = get2ByteInt(packet.getData(),15 + touchID*22);
			        		int zPt = get2ByteInt(packet.getData(),17 + touchID*22);
			        		
			        		if(xPt == 0 && yPt == 0 || zPt < 255)
			        			continue;
			        
			        		//System.out.println(xPt + " = " + yPt + " = " + zPt);
			        		
			        		this.m_CurrentFrame.addPoint((int)(1024*(yPt/39935d)), (int)((768*(xPt-2048d)/(31743d-2048))), zPt);
			        		 
			        	}
			        	

			        	// this is a touch point, so send the current frame to the renderer
			        	this.m_FMTEventHandler.setCurrentFrame(this.m_CurrentFrame);
			        	
			        	m_CurrentFrame = new FMTFrame();
				        // Reset the length of the packet before reusing it.
				        packet.setLength(buffer.length);
			        	continue;
			        }
			        
			        // this is a UDP packet, so fill up the frame with spectrum data
			        for(int row = 33; row < 63;row++){
			        	// each row has 22 bytes
			        	N = (int)packet.getData()[0 + row*22];
			        	B = (int)packet.getData()[1 + row*22];
			        	
			        	m_CurrentFrame.setSignalValue(0+N*5, B-33, IntFromByteArray(packet.getData(),2+0*4+row*22));
			        	m_CurrentFrame.setSignalValue(1+N*5, B-33, IntFromByteArray(packet.getData(),2+1*4+row*22));
			        	m_CurrentFrame.setSignalValue(2+N*5, B-33, IntFromByteArray(packet.getData(),2+2*4+row*22));
			        	m_CurrentFrame.setSignalValue(3+N*5, B-33, IntFromByteArray(packet.getData(),2+3*4+row*22));
			        	m_CurrentFrame.setSignalValue(4+N*5, B-33, IntFromByteArray(packet.getData(),2+4*4+row*22));
			        }
			        
			        
			        
			        //m_CurrentFrame.setSignalValue(x, y, val);
			        
			        //System.out.println(packet.getLength());
			        //System.out.println(packet.getOffset());
			        //System.out.println(packet.getAddress().getHostName() + ": "+ msg);

			        // Reset the length of the packet before reusing it.
			        packet.setLength(buffer.length);
			      }
			    } catch (Exception e) {
			      System.err.println(e);
			      e.printStackTrace();
			    }
		
	}
}
