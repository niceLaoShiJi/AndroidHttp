
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUnite {
    String myurl;
    String mymethod;
    String myupload;
    String myProperty;
    Boolean judge;

    public String net(String url, String method, String upload, String Property, Boolean j) {
        String result = null;
        this.myurl = url;
        this.mymethod = method;
        this.myupload = upload;
        this.myProperty = Property;
        this.judge = j;

        try {
            URL url1 = new URL(myurl);
            HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
            con1.setDoOutput(judge);
            con1.setDoInput(true);
            con1.setReadTimeout(3000);
            con1.setRequestProperty("connection", myProperty);
            con1.setRequestMethod(mymethod);
            if(mymethod.equals("POST")){
                OutputStreamWriter op1 = new OutputStreamWriter(con1.getOutputStream(), "utf-8");
                BufferedWriter bw1 = new BufferedWriter(op1);
                bw1.write(myupload);
                bw1.flush();
            }
            InputStream in1 = con1.getInputStream();
            InputStreamReader isr1 = new InputStreamReader(in1, "utf-8");
            BufferedReader br1 = new BufferedReader(isr1);
            String line;
            while ((line = br1.readLine()) != null) {
                result = line;
            }
            br1.close();
            isr1.close();
            in1.close();

        } catch (IOException e) {

            e.printStackTrace();
            return "IOError";
        }
        return result;
    }

}


