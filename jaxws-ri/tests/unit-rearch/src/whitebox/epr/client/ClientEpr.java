/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package whitebox.epr.client;

import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import junit.framework.TestCase;
import testutil.EprUtil;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ClientEpr extends TestCase {
    public ClientEpr(String name) {
        super(name);
    }

    private static final String endpointAddress = "http://helloservice.org/Hello";
    private static final QName serviceName = new QName("http://helloservice.org/wsdl", "HelloService");
    private static final QName portName = new QName("http://helloservice.org/wsdl", "HelloPort");
    private static final QName portTypeName = new QName("http://helloservice.org/wsdl", "Hello");

    public void testEprWithDispatchWithoutWSDL() throws Exception{
        Service service = Service.create(serviceName);
        service.addPort(portName, javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
        Dispatch dispatch = service.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);
        w3cEprGettertest(dispatch, false);
        msEprGettertest(dispatch, false);
    }

    public void testEprWithDispatchWithWSDL() throws Exception{
        Service service = Service.create(getClass().getResource("../config/HelloService.wsdl"), serviceName);
        Dispatch dispatch = service.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);
        w3cEprGettertest(dispatch, true);
        msEprGettertest(dispatch, true);
    }

    public void testEprWithSEI() throws Exception{
        HelloService service = new HelloService();
        Hello hello = service.getHelloPort();
        w3cEprGettertest((BindingProvider)hello, true);
        msEprGettertest((BindingProvider)hello, true);
    }

    private void w3cEprGettertest(BindingProvider bp, boolean hasWSDL) throws Exception {
        //validate w3c epr
        W3CEndpointReference w3cEpr = (W3CEndpointReference) bp.getEndpointReference();
//        printEPR(w3cEpr);
        //assertTrue(EprUtil.validateEPR(w3cEpr,endpointAddress, serviceName, portName, portTypeName, hasWSDL));
        assertTrue(EprUtil.validateEPR(w3cEpr,endpointAddress, null,null,null, false));
        //validate ms epr
        MemberSubmissionEndpointReference msEpr = bp.getEndpointReference(MemberSubmissionEndpointReference.class);
//        printEPR(msEpr);
        assertTrue(EprUtil.validateEPR(msEpr,endpointAddress, serviceName, portName, portTypeName, hasWSDL));

    }

    private void msEprGettertest(BindingProvider bp, boolean hasWSDL) throws Exception {
        Service service = Service.create(serviceName);
        service.addPort(portName, javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
        Dispatch dispatch = service.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);

        //validate ms epr
        MemberSubmissionEndpointReference msEpr = bp.getEndpointReference(MemberSubmissionEndpointReference.class);
//        printEPR(msEpr);
        assertTrue(EprUtil.validateEPR(msEpr,endpointAddress, serviceName, portName, portTypeName, hasWSDL));
        W3CEndpointReference w3cEpr = bp.getEndpointReference(W3CEndpointReference.class);
//        printEPR(w3cEpr);
//        assertTrue(EprUtil.validateEPR(w3cEpr,endpointAddress, serviceName, portName, portTypeName, hasWSDL));
        assertTrue(EprUtil.validateEPR(w3cEpr,endpointAddress, null,null,null, false));
    }

    private static void printEPR(EndpointReference epr) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult sr = new StreamResult(bos);
        epr.writeTo(sr);
        bos.flush();
        System.out.println(bos);
    }


}