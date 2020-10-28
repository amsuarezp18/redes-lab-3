package laboratorio;

public class Server {

private static final String RUTA = "./videos";
	
	public Server() {
		
		// Direcciones IP Multicast
		// Son usadas para la comunicación one-to-many y many-to-many sobre una red IP
		String[] grupos = {"225.6.7.8","224.3.29.71","224.22.65.7"};
		try {
			// Número de canales 
			for(int i=0;i<3;i++) 
			{ 
				new Thread(new Canal(RUTA+"/video"+i+".mp4",i+1,grupos[i])).start();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}
