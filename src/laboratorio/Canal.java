package laboratorio;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class Canal implements Runnable {
	String ruta;
	int canal;
	String ip;
	// Valor maximo en bytes para enviar data packets.
	static final int SIZE = 65500;
	
	public Canal(String pRuta, int pCanal, String pIp) {
		this.ruta = pRuta;
		this.canal = pCanal;
		this.ip = pIp;
	}

	public void run() {
		try {
			//UDP multicasting para ipv4.
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.out.println("Canal "+ canal + " listo");
			// Use de open cv para mostrar video
			
			// Creación de objecto Packet
			DatagramPacket dgp;
			
			// IP address donde el socket va a ser creado en UDP multicasting ( 2400-239)
			InetAddress addr;
			// Puerto, dirección donde va a ser realmente creado
			int port = 50005;
			
			// Video format 
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			
			Mat frame = new Mat();
			VideoCapture camera = new VideoCapture(this.ruta);
			
			// Arreglo para guardar el video
			byte[] data = new byte[SIZE];

			// Establecer dirección que va a usar
			addr = InetAddress.getByName(this.ip);
			// Creación del socket para la comunicación
			MulticastSocket socket = new MulticastSocket();

			
			final DatagramPacket packet = new DatagramPacket(data, data.length);
			// Como la información siempre va a estar siendo almacenada, queremos que se 
			// enviar a traves del socket continuamente.
			
			while (true) {
				// Leemos la información del frame
				if (camera.read(frame)) {
					// Guardamos la información en un array
					MatOfByte mob=new MatOfByte();
					Imgcodecs.imencode(".jpg", frame, mob);
					byte ba[]=mob.toArray();
					ByteArrayInputStream bas = new ByteArrayInputStream(ba);
					// Lee el siguiente chunk.
					bas.read(data, 0, data.length);
					// Creación de un paquete sobre la dirección y el puerto que definimos antes
					dgp = new DatagramPacket (data,data.length,addr,port);
					// se envia
					socket.send(dgp);
				}
				else {
					
					camera = new VideoCapture(this.ruta);
				}
			}
		}
		catch (UnknownHostException e) {
			System.out.println(e);
		} catch (SocketException e1) {
			System.out.println(e1);
		} catch (IOException e2) {
			System.out.println(e2);
		}
	}
}
