package org.cloudifysource.restDoclet.generation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

import org.cloudifysource.restDoclet.docElements.DocParameter;
import org.cloudifysource.restDoclet.docElements.DocRequestParamAnnotation;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Primitives;
import com.sun.javadoc.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.cloudifysource.restDoclet.constants.RestDocConstants.*;

/**
 * @author edward
 */
public class QueryParamGenerator {

  private static final Collection<String> wrapperClassNames;

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

  public DocParameter createParam(final Parameter parameter, final DocRequestParamAnnotation annotation, final String location)
      throws ClassNotFoundException, IntrospectionException {
    final String typeName;
    final Type type = parameter.type();

    if (type.isPrimitive() || wrapperClassNames.contains(type.qualifiedTypeName())) {
      typeName = type.simpleTypeName();
    }
    else if (List.class.getName().equals(type.qualifiedTypeName())) {
      if (type instanceof ParameterizedType) {
        ParameterizedType pType = (ParameterizedType) type;
        typeName = "List&lt;" + pType.typeArguments()[0].qualifiedTypeName() + "&gt;";
      }
      else {
        typeName = "List&lt;?&gt;";
      }
    }
    else if (Set.class.getName().equals(type.qualifiedTypeName())) {
      if (type instanceof ParameterizedType) {
        ParameterizedType pType = (ParameterizedType) type;
        typeName = "Set&lt;" + pType.typeArguments()[0].qualifiedTypeName() + "&gt;";
      }
      else {
        typeName = "Set&lt;?&gt;";
      }
    }
    else {
      typeName = type.qualifiedTypeName();
    }

    final DocParameter docParameter = new DocParameter(parameter.name(), typeName, location, annotation);
    if (paramsDocumentation.containsKey(parameter.name())) {
      docParameter.setDescription(paramsDocumentation.get(parameter.name()));
    }
    return docParameter;
  }

  public List<DocParameter> createCommandParams(Parameter parameter) throws ClassNotFoundException, IntrospectionException {
    final List<DocParameter> parameters = newArrayList();
    final BeanInfo info = Introspector.getBeanInfo(Class.forName(parameter.type().qualifiedTypeName()), Object.class);
    for (PropertyDescriptor prop : info.getPropertyDescriptors()) {
      final DocParameter docParameter = new DocParameter(prop.getName(), prop.getName(), prop.getPropertyType().getName(), LOCATION_QUERY);
      if (paramsDocumentation.containsKey(prop.getName())) {
        docParameter.setDescription(paramsDocumentation.get(parameter.name()));
      }
      parameters.add(docParameter);
    }
    return parameters;
  }
}
