package laboratorio;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

public class Cliente {
	static int port = 50005;
	static int canal;
	static InetAddress group;
	static final int SIZE = 65500; 
 
	public static void main(String args[]) throws Exception
	{
		String[] grupos = {"225.6.7.8","224.3.29.71","224.22.65.7"};
		System.out.println("Escriba el canal al que desea conectarse(1,2,3)");
		Scanner lectorConsola = new Scanner(System.in);
		canal = lectorConsola.nextInt(); 

		System.setProperty("java.net.preferIPv4Stack", "true");

		group = InetAddress.getByName(grupos[canal-1]);
		MulticastSocket mSocket = new MulticastSocket(port);
		mSocket.setReuseAddress(true);
		mSocket.joinGroup(group);

		System.out.println("Conectado");

		JFrame jframe = new JFrame();
		jframe.setSize(640,360);
		JLabel vidpanel = new JLabel();
		jframe.setTitle("Video en canal "+canal);
		JPanel x = new JPanel();
		x.setLayout(new BorderLayout());
		JButton l = new JButton("<");

		l.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mSocket.leaveGroup(group);
					canal = Math.max(1, canal-1);
					group = InetAddress.getByName(grupos[canal-1]);
					mSocket.joinGroup(group);
					jframe.setTitle("Video en canal  "+canal);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}	
		});

		JButton r = new JButton(">");
		r.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mSocket.leaveGroup(group);
					canal = Math.min(3, canal+1);
					group = InetAddress.getByName(grupos[canal-1]);
					mSocket.joinGroup(group);
					jframe.setTitle("Canal "+canal);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
		});

		x.add(vidpanel,BorderLayout.CENTER);
		x.add(r,BorderLayout.EAST);
		x.add(l,BorderLayout.WEST);
		jframe.getContentPane().add(x);

		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jframe.setVisible(true);
		byte[] receiveData = new byte[SIZE];


		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		while (true)
		{
			mSocket.receive(receivePacket);
			byte[] recv = receivePacket.getData();
			ByteArrayInputStream bas = new ByteArrayInputStream(recv);
			BufferedImage bi=ImageIO.read(bas);
			ImageIcon image =  new ImageIcon(bi);
			vidpanel.setIcon(image);
			vidpanel.repaint();
		}
	}
}


