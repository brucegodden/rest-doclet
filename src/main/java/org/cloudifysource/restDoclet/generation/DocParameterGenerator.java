package org.cloudifysource.restDoclet.generation;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.springframework.util.ClassUtils;

import com.sun.javadoc.MethodDoc;

/**
 * @author edward
 */
public class DocParameterGenerator {
  public DocParameter extractDocInfoFromMethod(MethodDoc methodDoc) throws ClassNotFoundException {
    Class returnClass = ClassUtils.forName(methodDoc.returnType().qualifiedTypeName(), null);
    DocParameter docParam = new DocParameter(methodDoc.name(), returnClass, "RequestParam");
    docParam.setDescription(methodDoc.commentText());
    return docParam;
  }
}
