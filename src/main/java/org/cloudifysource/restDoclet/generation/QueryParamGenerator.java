package org.cloudifysource.restDoclet.generation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;
import org.apache.commons.lang.ClassUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Primitives;
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

  final Map<String, String> paramsDocumentation;

  public QueryParamGenerator(final RestAnnotations restAnnotations) {
    paramsDocumentation = restAnnotations.paramsDocumentation();
  }

  public List<DocParameter> createParamList(Parameter parameter, DocRequestParamAnnotation annotation) throws ClassNotFoundException, IntrospectionException {
    List<DocParameter> parameters = new ArrayList<DocParameter>();
    Type type = parameter.type();
    if (type.isPrimitive() || wrapperClassNames.contains(type.qualifiedTypeName())) {
      parameters.add(new DocParameter(parameter.name(), type.qualifiedTypeName(), annotation));
    }
    else if (List.class.getName().equals(type.qualifiedTypeName())) {
      parameters.add(new DocParameter(parameter.name(), "List<?>", annotation));
    }
    else if (Set.class.getName().equals(type.qualifiedTypeName())) {
      parameters.add(new DocParameter(parameter.name(), "Set<?>", annotation));
    }
    else {
      BeanInfo info = Introspector.getBeanInfo(Class.forName(type.qualifiedTypeName()), Object.class);
      for (PropertyDescriptor prop : info.getPropertyDescriptors()) {
        parameters.add(new DocParameter(prop.getName(), prop.getPropertyType().toString(), annotation));
      }
    }
    return parameters;
  }

  public DocParameter createParam(Parameter parameter, DocRequestParamAnnotation annotation) throws ClassNotFoundException, IntrospectionException {
    final String typeName;
    final Type type = parameter.type();

    if (type.isPrimitive() || wrapperClassNames.contains(type.qualifiedTypeName())) {
      typeName = type.simpleTypeName();
    }
    else if (List.class.getName().equals(type.qualifiedTypeName())) {
      typeName = "List<?>";
    }
    else if (Set.class.getName().equals(type.qualifiedTypeName())) {
      typeName = "Set<?>";
    }
    else {
      typeName = type.qualifiedTypeName().startsWith("java.lang.")
          ? type.simpleTypeName()
          : type.qualifiedTypeName();
    }

    final DocParameter docParameter = new DocParameter(parameter.name(), typeName, annotation);
    if (paramsDocumentation.containsKey(parameter.name())) {
      docParameter.setDescription(paramsDocumentation.get(parameter.name()));
    }
    return docParameter;
  }
}
