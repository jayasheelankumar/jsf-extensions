/*
 * PartialTraversalLifecycle.java
 *
 * Created on May 15, 2006, 4:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.faces.extensions.avatar.lifecycle;

import com.sun.faces.extensions.avatar.components.PartialTraversalViewRoot;
import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import java.util.Map;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ResponseWriter;

/**
 *
 * <p>In order to preserve the ability to use server side template text
 * to aid in formatting the AJAX response, it is necessary to allow the
 * <i>render response phase</i> to execute "mostly" normally.  The
 * departures from normalcy include:</p>
 * 
 * 	<ol>

	  <li><p>The template text above and belowthe &lt;f:view&gt; tag
	  must be discarded.</p></li>

	  <li><p>Any output from
	  <code>ResponseWriter.startDocument()</code> and
	  <code>endDocument()</code> must be discarded.</p></li>

	  <li><p>A distinct set of subtrees of the view are rendered,
	  rather than the whole view.</p></li>
 
	</ol>
 *
 * <p>To accomodate these vagaries, this custom <code>Lifecycle</code>
 * implementation, in the {@link #render} method, if the {@link
 * com.sun.faces.extensions.avatar.lifecycle.AsyncResponse#isAjaxRequest}
 * method returns <code>true</code>, replaces the
 * <code>ResponseWriter</code> and <code>ResponseStream</code> in the
 * current <code>FacesContext</code> with instances that take no action
 * before calling the original <code>render()</code> method.  Otherwise,
 * it simply calls the parent implementation.  Upon return from the render
 * method it replaces the <code>ResponseWriter</code> and
 * <code>ResponseStream</code> instance with their original values.  The
 * vital necessity of this operation being done in a custom
 * <code>Lifecycle</code> vs a mere <code>PhaseListener</code>: there is
 * no control over the ordering in which <code>PhaseListener</code>s are
 * invoked.  Therefore, we need to ensure that <b>none</b> of them write
 * to the response, because doing so would confuse the client.</p>
 *
 * @author  edburns
 */
public class PartialTraversalLifecycle extends Lifecycle {
    
    private Lifecycle parent = null;
    private boolean redundantPhaseListenersRemoved = false;

    public PartialTraversalLifecycle(Lifecycle parent) {
        this.parent = parent;
    }

    public void execute(FacesContext context) throws FacesException {
        if (!redundantPhaseListenersRemoved) {
            PhaseListener [] listeners = getPhaseListeners();
            for (int i = 0; i < listeners.length; i++) {
               //test if listeners[i] has any duplicates later in the array,
               //and, if so, remove listeners[i]
               for (int j = i + 1; j < listeners.length; j++) {
                   if (listeners[i].getClass().getName().equals(listeners[j].getClass().getName()) || listeners[i].equals(listeners[j])){
                        removePhaseListener(listeners[i]);
                        break;  //go to the next i
                   }
               }
            }
            redundantPhaseListenersRemoved = true;
        }
        AsyncResponse async = AsyncResponse.getInstance();
        if (AsyncResponse.isAjaxRequest()) {
            async.installOnOffResponse(context);
            // Allow writing to the response during the "execute"
            // portion of the lifecycle.
            async.setOnOffResponseEnabled(true);
        }
        try {
            parent.execute(context);
        }
        finally {
            PartialTraversalViewRoot root =async.getPartialTraversalViewRoot();
            if (null != root) {
                root.postExecuteCleanup(context);
            }
        }
    }
    
    public void render(FacesContext context) throws FacesException {
        if (!AsyncResponse.isAjaxRequest()) {
            parent.render(context);
            return;
        }
        AsyncResponse async = AsyncResponse.getInstance();
        PartialTraversalViewRoot root =
                async.getPartialTraversalViewRoot();
        ResponseWriter writer = null;
        String state = null;
        boolean isRenderAll = async.isRenderAll();

        try {

            // Don't allow any content between now and the call
            // to PartialTraversalViewRoot.encodeBegin() to be 
	    // written to the response.
            async.setOnOffResponseEnabled(false);
            parent.render(context);
            
            if (isRenderAll) {
                root.encodePartialResponseEnd(context);
            }
            
            // If we rendered some content
            if (!async.isRenderNone()) {
                writer = async.getResponseWriter();
                if (!(root instanceof UIComponent)) {
                    throw new IllegalStateException("Class returned from" +
				" AsyncResponse.getPartialTraversalViewRoot" +
						    " must be a UIComponent");
                }
                writer.startElement("state", (UIComponent) root);
                state = async.getViewState(context);
                writer.write("<![CDATA[" + state + "]]>");
                writer.endElement("state");
                writer.endElement("partial-response");
            }
            
        }
        catch (IOException ioe) {
            // PENDING edburns
        }
        finally {
            async.removeOnOffResponse(context);
            AsyncResponse.clearInstance();
        }
        
    }
    

    public void removePhaseListener(PhaseListener phaseListener) {
        parent.removePhaseListener(phaseListener);
    }

    public void addPhaseListener(PhaseListener phaseListener) {
        if (!parentHasListener(phaseListener)) {
            parent.addPhaseListener(phaseListener);
        }
    }

    public PhaseListener[] getPhaseListeners() {
        PhaseListener [] result = parent.getPhaseListeners();
        return result;
    }
    
    private boolean parentHasListener(PhaseListener listener) {
        boolean result = false;
        PhaseListener [] listeners = getPhaseListeners();
        
        for (PhaseListener cur : listeners) {
           if (cur.getClass().getName().equals(listener.getClass().getName()) || cur.equals(listener)){
                result = true;
                break;
            }
        }
        
        return result;
    }
    
  
}
