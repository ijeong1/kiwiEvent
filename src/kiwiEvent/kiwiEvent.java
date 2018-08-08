package kiwiEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;


public class kiwiEvent {
	public static void main(String[] args) throws IOException, InterruptedException , ElementNotFoundException{
		 String myID = "myID";
	        String myPW = "myPW";
	        String targetURL = "http://www.xxdisk.com/";
	        String eventURL = "http://www.xxdisk.com/event.php";

	        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	        webClient.getOptions().setJavaScriptEnabled(true);
	        webClient.getOptions().setCssEnabled(false); // I think this speeds the thing up
	        webClient.getOptions().setRedirectEnabled(true);
	        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	        webClient.getCookieManager().setCookiesEnabled(true);
	        webClient.waitForBackgroundJavaScript(1000);
	        //webClient.getOptions().setUseInsecureSSL(true);
	        webClient.getOptions().setThrowExceptionOnScriptError(false);

	        //Log off
	        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
	        // disable caching
	        webClient.getCache().setMaxSize(0);
	        webClient.getOptions().setThrowExceptionOnScriptError(false);
	        webClient.getOptions().setPopupBlockerEnabled(true);

	        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
	        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
	        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

	        HtmlPage page=null;
	        try {
	            page = webClient.getPage(targetURL);
	        } catch (FailingHttpStatusCodeException e) {
	            e.printStackTrace();
	        } catch (MalformedURLException e){
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        HtmlForm form = page.getFormByName("login_form");
	        // filtering only those elements which are for input
	        List<HtmlInput> inputs = getInputs(form);
	        // initializing post string
	        //searching for 'username'&'password',setting,and constructing the whole post
	        for ( int i = 0; i < inputs.size(); i++ ){
	            // single input element
	            HtmlInput input = inputs.get(i);
	            if ( input.getNameAttribute().equals("userid")){
	                input.setValueAttribute(myID);
	                //System.out.println("username setted to ");
	            } else if ( input.getNameAttribute().equals("userpw")){
	                input.setValueAttribute(myPW);
	                //System.out.println("password setted to your password");
	            }
	        }

	        HtmlButton submitButton = (HtmlButton)page.createElement("button");
	        submitButton.setAttribute("type", "submit");
	        form.appendChild(submitButton);
	        page = submitButton.click();

	        Thread.sleep(1000);
	        CookieManager CM = webClient.getCookieManager(); //WC = Your WebClient's name
	        Set<Cookie> set = CM.getCookies();
	        StringBuilder cookieHeader = new StringBuilder();



	        CollectingAlertHandler alertHandler = new CollectingAlertHandler();
	        webClient.setAlertHandler(alertHandler);

	        try {
	            page = webClient.getPage(eventURL);
        	}catch(Exception e){
        		System.out.println("error");
        	}
	        List<String> alertmsgs = new ArrayList<String>();
	        alertmsgs = alertHandler.getCollectedAlerts();
	        System.out.println(alertmsgs.get(0).toString());


	       webClient.close();
	       //urlList.clear();
	       System.out.println("================Finished================");
	       CM.clearCookies();
	       System.exit(0);
	}

	private static List<HtmlInput> getInputs(DomNode node){
        Iterable<DomNode> children = node.getChildren();
        List<HtmlInput> inputList = new ArrayList<HtmlInput>();
        for ( DomNode child : children){
            if ( child instanceof HtmlInput){
                inputList.add((HtmlInput)child);
            }
            if ( child.hasChildNodes()){
                List<HtmlInput> returnValue = getInputs(child);
                for ( DomNode pass : returnValue){
                    inputList.add((HtmlInput)pass);
                }
            }
        }
        return inputList;
    }
}
