package com.itextpdf.pdfchain;/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
import com.itextpdf.pdfchain.blockchain.IBlockChain;
import com.itextpdf.pdfchain.blockchain.MultiChain;
import com.itextpdf.pdfchain.blockchain.Record;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.itextpdf.pdfchain.pdfchain.PdfChain;
import com.itextpdf.pdfchain.sign.AbstractExternalSignature;
import com.itextpdf.pdfchain.sign.DefaultExternalSignature;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class BasicFunctionalityTest {

    @BeforeClass
    public static void beforeClass() {
    }

    @Test
    public void putOnChainTest() throws Exception {
        IBlockChain mc = new MultiChain(
                "http://127.0.0.1",
                9740,
                "chain-dev",
                "stream-dev",
                "multichainrpc",
                "TVnqseBcHsYjeTU1ACVmF75nCviRJ9UmLdubGApjtsD");

        InputStream keystoreInputStream = BasicFunctionalityTest.class.getClassLoader().getResourceAsStream("ks");
        InputStream inputFileStream = BasicFunctionalityTest.class.getClassLoader().getResourceAsStream("input.pdf");

        AbstractExternalSignature sgn = new DefaultExternalSignature(keystoreInputStream, "demo", "password");

        PdfChain chain = new PdfChain(mc, sgn);

        // put a document on the chain
        boolean wasAdded = chain.put(inputFileStream);
        assertTrue(wasAdded);

        // check whether the chain now contains this value
        boolean isEmpty = chain.get("z�L{�Wd=��\u007F\u0010��G�").isEmpty();
        assertFalse(isEmpty);

        // check signature
        for(Record r : chain.get("z�L{�Wd=��\u007F\u0010��G�")){
            if(chain.isSigned(r, sgn.getPublicKey()))
                System.out.println("This record is signed");
        }

    }
}
