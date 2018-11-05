/**
 * 
 */
package ivoox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;

/** TestURLs.java
 * @author gvillahermosac
 *
 */
public class Ivoox {

//    private static final String cadenaABuscar = "inglesparaprincipiantes";
//    private static final String cadenaABuscar = "ingles";
    private static final String cadenaABuscar = "inglesnivelavanzado";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        HttpURLConnection httpConn = null;
        String url		= "http://www.ivoox.com/ingles-para-principiantes-113_mf_21559726_feed_1.mp3";
        url				= "http://www.ivoox.com/ingles-nivel-intermedio-176_mf_22111472_feed_1.mp3";
        url				= "http://www.ivoox.com/ingles-nivel-avanzado-195_mf_22330101_feed_1.mp3";
//        String urlBase	= "http://www.ivoox.com/ingles-para-principiantes-";
//        String urlBase	= "http://www.ivoox.com/ingles-nivel-intermedio-";
        String urlBase	= "http://www.ivoox.com/ingles-nivel-avanzado-";
        int iniId		= 182;
        int iniIndex	= 22329661;
        int maxId		= 195;
        int maxIndex	= 22330101;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.indra.es", 8080));
        try {
        	for (int i = iniIndex; (i<maxIndex+1 && iniId < maxId+1); i++) {
        		url = urlBase + iniId + "_mf_" + i + "_feed_1.mp3";
	            Date inicio = new Date();
	            
	            // Comprobación de disponibilidad
	            URL urlSolicitud = new URL(url);
	            java.net.URLConnection urlConn = urlSolicitud.openConnection(proxy);
	            try {
		            if (urlConn instanceof HttpURLConnection) {
		                httpConn = (HttpURLConnection) urlConn;
		                httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0");
		                httpConn.setReadTimeout(3000);
		                int responseCode = httpConn.getResponseCode();
			            Date fin = new Date();
		            	System.out.println(url + " --> " + responseCode + " --> " + httpConn.getContentType() + " --> " + httpConn.getURL().getPath() + " ("+(fin.getTime()-inicio.getTime())+"ms)");
		                if (responseCode == HttpURLConnection.HTTP_OK
		                		&& httpConn.getURL().getPath().indexOf(cadenaABuscar) > 0) {
		    	            iniId++;
		                    descargarFichero(i, urlConn);
		                }
		            }
	            } catch (ConnectException ce) {
	            	System.out.println(url +"--> ERROR DE CONEXIÓN!! " + ce);
	            } catch (MalformedURLException mue) {
	            	System.out.println(url +"--> ERROR DE CONEXIÓN!! " + mue);
	            } catch (IOException ioe) {
	            	System.out.println(url +"--> ERROR DE CONEXIÓN!! " + ioe);
	            } finally {
    	        	if (httpConn!=null){
    	        		httpConn.disconnect();
    	        	}
    	        	if (urlConn!=null){
    	        		((HttpURLConnection) urlConn).disconnect();
    	        	}
	            }
        	}
        } catch (Exception e) {
        	System.out.println(e);
        } finally {
        	if (httpConn!=null){
        		httpConn.disconnect();
        	}
        }
    }

	/**
	 * @param iniId
	 * @param i
	 * @param urlConn
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void descargarFichero(int i, java.net.URLConnection urlConn) throws IOException, FileNotFoundException {
		//Directorio destino para las descargas
		String folder = "D:/audiosDescargadosIvoox/";
		 
		//Crea el directorio de destino en caso de que no exista
		File dir = new File(folder);
		 
		if (!dir.exists())
		  if (!dir.mkdir())
		    return; // no se pudo crear la carpeta de destino
		
		
		// acceso al contenido web
		InputStream is = urlConn.getInputStream();

		// Fichero en el que queremos guardar el contenido
		int indexOf = urlConn.getURL().getPath().indexOf(cadenaABuscar);
		String fileName = urlConn.getURL().getPath().substring(indexOf, urlConn.getURL().getPath().length());
//		File file = new File(folder + "/ingles-para-principiantes-" + iniId + "_mf_" + i + "_feed_1.mp3");
		File file = new File(folder + fileName);
		if (!file.exists()) {
			System.out.print("Descargando " + file.getName());
			OutputStream fos = new FileOutputStream(file);
	
			// buffer para ir leyendo.
			byte [] array = new byte[1000];
	
			// Primera lectura y bucle hasta el final
			int leido = is.read(array);
			int x = 0;
			while (leido > 0) {
			   fos.write(array,0,leido);
			   leido=is.read(array);
				if (x%250 == 0) {
					System.out.print("|");
				}
				x++;
			}
			// Cierre de fichero.
			fos.close();
			System.out.println();
		}

		// Cierre de conexion.
		is.close();
	}

}
