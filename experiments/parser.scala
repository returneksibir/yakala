/* 
Copyright (c) 2008 Florian Hars, BIK Aschpurwis+Behrens GmbH, Hamburg 
Copyright (c) 2002-2008 EPFL, Lausanne, unless otherwise specified. 
All rights reserved. 

This software was developed by the Programming Methods Laboratory of the 
Swiss Federal Institute of Technology (EPFL), Lausanne, Switzerland. 

Permission to use, copy, modify, and distribute this software in source 
or binary form for any purpose with or without fee is hereby granted, 
provided that the following conditions are met: 

   1. Redistributions of source code must retain the above copyright 
      notice, this list of conditions and the following disclaimer. 

   2. Redistributions in binary form must reproduce the above copyright 
      notice, this list of conditions and the following disclaimer in the 
      documentation and/or other materials provided with the distribution. 

   3. Neither the name of the EPFL nor the names of its contributors 
      may be used to endorse or promote products derived from this 
      software without specific prior written permission. 


THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE 
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY 
OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF 
SUCH DAMAGE. 
*/

import scala.xml.parsing.FactoryAdapter
import scala.xml._
import org.xml.sax.InputSource
import javax.xml.parsers.SAXParser
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl

class TagSoupFactoryAdapter extends FactoryAdapter {
  val parserFactory = new SAXFactoryImpl()
  parserFactory.setNamespaceAware(false)

  val emptyElements = Set("area", "base", "br", "col", "hr",
			  "img", "input", "link", "meta", "param")
  
  def nodeContainsText(localName: String) = !(emptyElements contains localName)

  def createNode(pre:String, label: String, attrs: MetaData,
		 scpe: NamespaceBinding, children: List[Node]): Elem = {
    Elem(pre, label, attrs, scpe, children:_*);
  }

  def createText(text: String) = Text(text);

  def createProcInstr(target: String, data: String) = Nil
  
  override def loadXML(source: InputSource) = {
    val parser: SAXParser = parserFactory.newSAXParser()

    scopeStack.push(TopScope)
    parser.parse(source, this)
    scopeStack.pop
    rootElem
  }
}

object deneme extends Application {
  val node = new TagSoupFactoryAdapter load "http://www.returneksibir.com/kitapsever"
  println("printing node" + node)
}