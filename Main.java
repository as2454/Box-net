/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package boxnet;
import java.io.*;
import java.util.*;

/**
 *
 * @author Anandram
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static HashMap config;
    public static int num=0;
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String[] over =/*null;//*/{"live","publish"};
        HashMap c2=load_config("test.txt",over);//This is just to show we can load it to an object
        //If required this object can be passed as a parameter to the access function to retrieve data
        //from this object rather than the default config variable.
        String a;
        a=(String) access("CONFIG.ftp.path");
        group ch=(group) access("CONFIG.common");
        System.out.println(a);
        System.out.println(ch.m.get("basic_size_limit"));//We can get a list of keys and iterate
        //through them and get the required output.
    }
    public static String[] spl(String var)
    {
        String ne=null;
        for(int i=0;i<var.length();i++)
            if(var.charAt(i)=='.')
                ne=ne+',';
            else
                ne=ne+var.charAt(i);
        return ne.split(",");
    }

    public static Object access(String var){
        group g=new group();
        //Assuming the input is always going to be CONFIG.group.param
        String gr;
        gr=spl(var)[1];
        if(spl(var).length<3)
        {
            if(config.containsKey(gr))
                return config.get(gr);
            else
                return null;
        }
        else
        {
            String param=spl(var)[2];
            if(config.containsKey(gr)){
                g=(group) config.get(gr);
                if(g.m.containsKey(param))
                    return g.m.get(param);
                else
                    return null;
            }
            else
                return null;
        }
    }

    public static HashMap load_config(String filename,String[] overrides) throws FileNotFoundException, IOException{
        config=new HashMap();
        FileInputStream fstream = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        group conf=new group();
        conf.size=0;
        String temp;
        while((temp=r.readLine())!=null)
        {
            if(temp.charAt(0)=='[')
            {
                if(num>0){
                    config.put(conf.name,conf);
                }
                conf=new group();
                num++;
                conf.size=0;
                conf.name=temp.substring(1,temp.length()-1);
                conf.m=new HashMap();
            }
            else if(temp.charAt(0)==';')
            {
                continue;
            }
            else if(temp.length()!=0){
                String param;
                String over="";
                String value;
                param=temp.split("=")[0];
                value=temp.split("=")[1];
                value=value.split(";")[0];
                value=value.substring(1,value.length());
                if(param.split("<").length>1)
                    over=param.split("<")[1];
                if(!over.isEmpty()){
                    over=over.substring(0,over.length()-2);
                    param=param.split("<")[0];
                    if(!check(over,overrides))
                        continue;
                    //If the override does not exist, neglect the parameter,value assuming defaults exists
                }
                else
                    param=param.substring(0,param.length()-1);
                conf.m.put(param, value);
                //Not checking for the over ride because the last one takes priority
                conf.size++;
            }
        }
        config.put(conf.name,conf);//the last group before end of file.
        return config;
    }
    public static boolean check(String var,String[] arr){
        if(arr==null)
            return false;
            for(int i=0;i<arr.length;i++)
                if(arr[i].equals(var))
                    return true;
            return false;
    }
}
