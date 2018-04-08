
/**
 * Created by Yanye on 3/12/2018.
 */

public class RequestBuilder {

    private StringBuilder builder;
    private final int SIZE=256;
    public RequestBuilder(){
        builder=new StringBuilder(this.SIZE);
    }

    public RequestBuilder(int size){
        if(size>this.SIZE){
            builder=new StringBuilder(size);
        }else{
            builder=new StringBuilder(this.SIZE);
        }
    }

    public void addParams(String key,String value){
        builder.append(key+"="+value+"&");
    }

    @Override
    public String toString() {
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
