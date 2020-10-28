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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Client {

	// Puerto
	static int port = 50005;
	static int canal;
	static InetAddress group;
	// Sample rate el mismo que el servidor.
	static final int SIZE = 65500; 
 
	public static void main(String args[]) throws Exception
	{
		String[] grupos = {"225.6.7.8","224.3.29.71","224.22.65.7"};
		System.out.println("¿A qué canal desea conectarse?(1,2,3)");
		Scanner lectorConsola = new Scanner(System.in);
		canal = lectorConsola.nextInt(); 

		// Establecer ipv4 para que compile, ya que ipv6 no soporta multicasting
		System.setProperty("java.net.preferIPv4Stack", "true");

		// Creación del grupo multicasting.
		group = InetAddress.getByName(grupos[canal-1]);
		// Creación del socket
		MulticastSocket mSocket = new MulticastSocket(port);
		mSocket.setReuseAddress(true);
		// Se conecta al grupo 
		mSocket.joinGroup(group);

		System.out.println("Conectado");
		
		// Ajustes para recibir información del servidor
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
		
		// Guardar la información 	que se va a recibir
		byte[] receiveData = new byte[SIZE];

		// Creación del paquete que se va a recibir
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		// Recibe información del socket 
		while (true)
		{
			// Recibe el paquete 
			mSocket.receive(receivePacket);
			// Se debe convertir a bytes para que luego se pase al codificador para ver el video
			byte[] recv = receivePacket.getData();
			// Obtener el stream de ese paquete. 
			ByteArrayInputStream bas = new ByteArrayInputStream(recv);
			BufferedImage bi=ImageIO.read(bas);
			ImageIcon image =  new ImageIcon(bi);
			// Pintar nuestro video
			vidpanel.setIcon(image);
			vidpanel.repaint();
		}
	}
}
