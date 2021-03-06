/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package fireAjaxTrans;

import com.sun.faces.extensions.avatar.lifecycle.AsyncResponse;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This bean has the methods that are used to illustrate
 *  the <code>DynaFaces.fireAjaxTransaction</code> event.
 * <p/>
 *
 */

public class Bean {
    
    private static final Logger LOGGER = Logger.getLogger("fireAjaxTrans");
    
    //
    // Relationship Instance Variables
    //
    
    private Map<String, String> options = null;
    
    //
    // Constructors
    //
    
    public Bean() {
        loadOptions();
    }
    
    private String fireOptions = null;
    
    public void setFireOptions(String fireOptions) {
        this.fireOptions = fireOptions;
    }
    
    public String getFireOptions() {
        return fireOptions;
    }
    
    public void getOptions(ActionEvent ae) {
        UICommand command = (UICommand)ae.getComponent();
        fireOptions = (String)options.get(command.getId());
        displayOptions();
    }
    
    public void reset(ActionEvent ae) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIForm form = (UIForm)context.getViewRoot().findComponent("form");
        if (form != null) {
            UIComponent component = form.findComponent("_0");
            setInitialRender(component);
        }
        fireOptions = null;
    }
    
    private void loadOptions() {
        options = new HashMap<String, String>();
        options.put("ajax1", "{execute:'_1,ajax1'}");
        options.put("ajax2", "{execute:'_6,_10,ajax2'}");
        options.put("ajax3", "{execute:'_5,ajax3'}");
        options.put("ajax4", "{execute:'_4,_9,ajax3'}");
        options.put("ajax5", "{execute:'ajax5',render:'_1'}");
        options.put("ajax6", "{execute:'ajax6',render:'_6'}");
        options.put("ajax7", "{execute:'ajax7,_10',render:'_5'}");
        options.put("ajax8", "{execute:'ajax8,_1,_2,_3,_4',render:'_1,_2,_3,_4'}"); 
    }
    
    private void displayOptions() {
        FacesContext context = FacesContext.getCurrentInstance();
        UIForm form = (UIForm)context.getViewRoot().findComponent("form");
        UIComponent component = form.findComponent("optionsTitle");
        component.setRendered(true);
        component = form.findComponent("fireOptions");
        component.setRendered(true);
    }
    
    private void setInitialRender(UIComponent component) {
        Iterator<UIComponent> kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            setInitialRender(kid);
        }
        if (component.getAttributes().get("styleClass") != null) {
            component.getAttributes().remove("styleClass");
        }
    }
}
