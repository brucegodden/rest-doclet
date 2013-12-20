package org.cloudifysource.restDoclet.generation;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.springframework.util.ClassUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Primitives;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

/**
 * @author edward
 */
public class QueryParamGenerator {

  static final Collection<String> wrapperClassNames;

  static {
    wrapperClassNames = Collections2.transform(Primitives.allWrapperTypes(), new Function<Class<?>, String>() {
      @Override
      public String apply(final Class<?> input) {
        return input.getName();
      }
    });
  }

  private DocParameterGenerator docParameterGenerator_;

  public QueryParamGenerator(DocParameterGenerator docParameterGenerator) {
    docParameterGenerator_ = docParameterGenerator;
  }

  public List<DocParameter> createParamList(Parameter parameter) throws ClassNotFoundException, IntrospectionException {
    List<DocParameter> parameters = new ArrayList<DocParameter>();
    Type type = parameter.type();
    if (type.isPrimitive() || wrapperClassNames.contains(type.qualifiedTypeName())) {
      Class<?> clazz = ClassUtils.forName(type.qualifiedTypeName(), null);
      parameters.add(new DocParameter(parameter.name(), clazz, "RequestParam"));
    }
    else if (List.class.getName().equals(type.qualifiedTypeName())) {
      parameters.add(new DocParameter(parameter.name(), List.class, "RequestParam"));
    }
    else if (Set.class.getName().equals(type.qualifiedTypeName())) {
      parameters.add(new DocParameter(parameter.name(), Set.class, "RequestParam"));
    }
    else {
      ClassDoc beanDoc = parameter.type().asClassDoc();
      for (MethodDoc methodDoc : beanDoc.methods()) {
        if (methodDoc.name().startsWith("get")) {
          parameters.add(docParameterGenerator_.extractDocInfoFromMethod(methodDoc));
        }
      }
    }
    return parameters;
  }

}
