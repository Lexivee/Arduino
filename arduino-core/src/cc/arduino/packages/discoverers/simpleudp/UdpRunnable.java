/*
* This file is part of Arduino.
*
* Arduino is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*
* As a special exception, you may use this file as part of a free software
* library without restriction.  Specifically, if other files instantiate
* templates or use macros or inline functions from this file, or you compile
* this file and link it with other files to produce an executable, this
* file does not by itself cause the resulting executable to be covered by
* the GNU General Public License.  This exception does not however
* invalidate any other reasons why the executable file might be covered by
* the GNU General Public License.
*
* Copyright 2013 Arduino LLC (http://www.arduino.cc/)
*/

package cc.arduino.packages.discoverers.simpleudp;

import cc.arduino.packages.BoardPort;
import processing.app.BaseNoGui;
import processing.app.Platform;
import processing.app.debug.TargetBoard;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

 
import java.util.*;



public class UdpRunnable implements Runnable {

	private boolean running = true;
	// local, thread save list
    public final List<BoardPort> udpBoardPorts = Collections.synchronizedList(new LinkedList<>());
	
	private int hasip(String ip) {
		int i = 0;
        for (BoardPort port : udpBoardPorts) {
          if (port.getAddress().equals(ip)) {
      		return i;
          }
		  i++;
        }
		
		return -1;
	}
	
	public void run(){
		System.out.println("MyRunnable running");
		
		while (running) 
		{
			try
			{
				DatagramSocket socket = new DatagramSocket(8531, InetAddress.getByName("0.0.0.0"));
				socket.setBroadcast(true);
				System.out.println("Listen on " + socket.getLocalAddress() + " from " + socket.getInetAddress() + " port " + socket.getBroadcast());
				byte[] buf = new byte[512];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				while (true) {
					System.out.println("Waiting for data");
					socket.receive(packet);
					System.out.print(packet.getLength());
     				System.out.print(" Data received from ");
					
                    String board = null;
					InetAddress senderip = packet.getAddress();
     				System.out.println(senderip);
					
					String msg = new String(packet.getData());
                    String[] lines = msg.split("\\n");
					
     				System.out.println("");
					
     				System.out.print(">>>");
     				System.out.print(lines[0]);
     				System.out.println("<<<<");
					
					// msg typ 1
					if (lines[0].equals("1")) {
						int portexists = hasip(senderip.toString().substring(1));
						if (portexists==-1) {
                          BoardPort port = new BoardPort();
	
                          port.setAddress(senderip.toString().substring(1));
                          port.setProtocol("network");
                          port.setOnlineStatus(true);
                          port.setLabel(lines[1]+" at "+senderip.toString());
	
                          udpBoardPorts.add(port);
						}
					}
				}

			}
            catch (UnknownHostException e) {
                e.printStackTrace();
			}
			catch ( Exception e )
			{
				running=false;
			}
		}
		
	}
	
	public void terminate()
	{
		running = false;
	}
	
	
}

/*
    even more simple device discovery protocol 
	
	send a broadcase to port 8531
	
	message format
	
	1\Ndisplayname\N
	
	1 - id of this message type
	    future protocols can choose different numbers to implement more details

    BoardPort needs lastseen-time-thing for removing not anymore pinging boards.		
	
	
*/
















