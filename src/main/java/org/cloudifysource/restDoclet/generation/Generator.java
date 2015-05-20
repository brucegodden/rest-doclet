/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.cloudifysource.restDoclet.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.cloudifysource.restDoclet.annotations.DocumentCommand;
import org.cloudifysource.restDoclet.constants.RestDocConstants;
import org.cloudifysource.restDoclet.docElements.*;
import org.cloudifysource.restDoclet.exampleGenerators.ExampleGenerator;
import org.cloudifysource.restDoclet.exampleGenerators.ObjectCreator;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Optional;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Generates REST API documentation in an HTML form. <br />
 * Uses velocity template to generate an HTML file that contains the
 * documentation.
 * <ul>
 * <li>To specify your sources change the values of
 * {@link RestDocConstants#SOURCES_PATH} and
 * {@link RestDocConstants#CONTROLLERS_PACKAGE}.</li>
 * <li>To specify different template path change the value
 * {@link RestDocConstants#VELOCITY_TEMPLATE_PATH}.</li>
 * <li>To specify the destination path of the result HTML change the value
 * {@link RestDocConstants#DOC_DEST_PATH}.</li>
 * </ul>
 * In default the Generator uses the velocity template
 * {@link RestDocConstants#VELOCITY_TEMPLATE_PATH} and writes the result to
 * {@link RestDocConstants#DOC_DEST_PATH}.
 *
 * @author yael
 */
public class Generator {
  private static final Logger logger = Logger.getLogger(Generator.class.getName());
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private final RootDoc documentation_;
  private String velocityTemplatePath;
  private String velocityTemplateFileName;
  private boolean isUserDefineTemplatePath = false;
  private String docPath;
  private String version;
  private String docCssPath;
  private static AnnotationReader annotationReader = new AnnotationReader();
  private static ExampleGenerator exampleGenerator = new ExampleGenerator(new ObjectCreator());

  /**
   *
   * @param rootDoc
   */
  public Generator(final RootDoc rootDoc) {
    documentation_ = rootDoc;
    setFlags(documentation_.options());
  }

  /**
   * <p>This class uses the annotationType() method of class DocAnnotation,
   * so if there is an annotation in the source with its class not in the class path,
   * a ClassCastException will be thrown.
   * <br>For example, in order to use the PreAuthorize annotation,
   * the spring-security-core JAR needs to be added to the class path.
   * <br><a href="http://stackoverflow.com/questions/5314738/javadoc-annotations-from-third-party-libraries">
   * related question in stackoverflow</a>
   */
  public static void main(final String[] args) {

    com.sun.tools.javadoc.Main.execute(new String[] {
        RestDocConstants.DOCLET_FLAG, RestDoclet.class.getName(),
        RestDocConstants.SOURCE_PATH_FLAG, RestDocConstants.SOURCES_PATH, RestDocConstants.CONTROLLERS_PACKAGE,
        RestDocConstants.VELOCITY_TEMPLATE_PATH_FLAG, RestDocConstants.VELOCITY_TEMPLATE_PATH,
        RestDocConstants.DOC_DEST_PATH_FLAG, RestDocConstants.DOC_DEST_PATH,
        RestDocConstants.DOC_CSS_PATH_FLAG, RestDocConstants.DOC_CSS_PATH,
        RestDocConstants.VERSION_FLAG, RestDocConstants.VERSION
        });
  }

  /**
   *
   * @param options
   */
  private void setFlags(final String[][] options) {
    int flagPos = 0;
    int contentPos = 1;
    for (int i = 0; i < options.length; i++) {
      String flagName = options[i][flagPos];
      String flagValue = null;
      if (options[i].length > 1) {
        flagValue = options[i][contentPos];
      }
      if (RestDocConstants.VELOCITY_TEMPLATE_PATH_FLAG.equals(flagName)) {
        velocityTemplatePath = flagValue;
        logger.log(Level.INFO, "Updating flag " + flagName + " value = " + flagValue);
      } else if (RestDocConstants.DOC_DEST_PATH_FLAG.equals(flagName)) {
        docPath = flagValue;
        logger.log(Level.INFO, "Updating flag " + flagName + " value = " + flagValue);
      } else if (RestDocConstants.VERSION_FLAG.equals(flagName)) {
        version = flagValue;
        logger.log(Level.INFO, "Updating flag " + flagName + " value = " + flagValue);
      } else if (RestDocConstants.DOC_CSS_PATH_FLAG.equals(flagName)) {
        docCssPath = flagValue;
        logger.log(Level.INFO, "Updating flag " + flagName + " value = " + flagValue);
      }
    }

    if (!StringUtils.isBlank(velocityTemplatePath)) {
      isUserDefineTemplatePath = true;
      int fileNameIndex = velocityTemplatePath.lastIndexOf("/");
      if (fileNameIndex == -1) {
        fileNameIndex = velocityTemplatePath.lastIndexOf("\\") ;
      }
      if (fileNameIndex == -1) {
        logger.log(Level.SEVERE, "Path/filename split problem for " + velocityTemplatePath);
      }
      velocityTemplateFileName = velocityTemplatePath.substring(fileNameIndex + 1);
      velocityTemplatePath = velocityTemplatePath.substring(0, fileNameIndex);
    } else {
      velocityTemplateFileName = RestDocConstants.VELOCITY_TEMPLATE_FILE_NAME;
      velocityTemplatePath = this.getClass().getClassLoader()
          .getResource(velocityTemplateFileName).getPath();
    }

    if (StringUtils.isBlank(docPath)) {
      docPath = RestDocConstants.DOC_DEST_PATH;
    }

    if (StringUtils.isBlank(version)) {
      version = RestDocConstants.VERSION;
    }

    if (StringUtils.isBlank(docCssPath)) {
      docCssPath = RestDocConstants.DOC_CSS_PATH;
    }
  }

  /**
   *
   * @throws Exception .
   */
  public void run() throws Exception {

    // GENERATE DOCUMENTATIONS IN DOC CLASSES
    ClassDoc[] classes = documentation_.classes();
    List<DocController> controllers = generateControllers(classes);
    controllers.sort((o1, o2) -> o1.getUri().compareTo(o2.getUri()));
    logger.log(Level.INFO, "Generated " + controllers.size()
        + " controllers, creating HTML documentation using velocity template.");

    // TRANSLATE DOC CLASSES INTO HTML DOCUMENTATION USING VELOCITY TEMPLATE
    String generatedHtml = generateHtmlDocumentation(controllers);

    // WRITE GENERATED HTML TO A FILE
    FileWriter velocityfileWriter = null;
    try {
      File file = new File(docPath);
      File parentFile = file.getParentFile();
      if (parentFile != null) {
        if (parentFile.mkdirs()) {
          logger.log(
              Level.FINEST,
              "The directory "
                  + parentFile.getAbsolutePath()
                  + " was created, along with all necessary parent directories.");
        }
      }
      logger.log(Level.INFO,
          "Write generated velocity to " + file.getAbsolutePath());
      velocityfileWriter = new FileWriter(file);
      velocityfileWriter.write(generatedHtml);
    } finally {
      if (velocityfileWriter != null) {
        velocityfileWriter.close();
      }
    }
  }

  /**
   * Creates the REST API documentation in HTML form, using the controllers'
   * data and the velocity template.
   *
   * @param controllers .
   * @return string that contains the documentation in HTML form.
   * @throws Exception .
   */
  public String generateHtmlDocumentation(final List<DocController> controllers)
      throws Exception {

    logger.log(Level.INFO, "Generate velocity using template: "
        + velocityTemplatePath
        + (isUserDefineTemplatePath ? File.separator
            + velocityTemplateFileName + " (got template path from user)"
            : "(default template path)"));

    Properties p = new Properties();
    p.setProperty("directive.set.null.allowed", "true");
    if (isUserDefineTemplatePath) {
      p.setProperty("file.resource.loader.path", velocityTemplatePath);
    } else {
      p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
      p.setProperty("classpath.resource.loader.class",
          ClasspathResourceLoader.class.getName());
    }

    Velocity.init(p);

    VelocityContext ctx = new VelocityContext();

    ctx.put("controllers", controllers);
    ctx.put("version", version);
    ctx.put("docCssPath", docCssPath);


    Writer writer = new StringWriter();

    Template template = Velocity.getTemplate(velocityTemplateFileName);
    template.merge(ctx, writer);

    return writer.toString();

  }

  private List<DocController> generateControllers(final ClassDoc[] classes)
      throws Exception {
    List<DocController> controllersList = new LinkedList<DocController>();
    for (ClassDoc classDoc : classes) {
      try {
        List<DocController> controllers = generateControllers(classDoc);
        if (controllers == null || controllers.isEmpty()) {
          continue;
        }
        controllersList.addAll(controllers);
      }
      catch (Exception e) {
        logger.log(Level.SEVERE, "Failed processing controller " + classDoc.qualifiedTypeName());
        throw e;
      }
    }
    return controllersList;
  }

  private List<DocController> generateControllers(final ClassDoc classDoc)
      throws Exception {
    List<DocController> controllers = new LinkedList<DocController>();
    RestAnnotations restAnnotations = annotationReader.read(
            Arrays.asList(classDoc.annotations()),
            Arrays.asList(classDoc.tags()));

    if (Utils.filterOutControllerClass(classDoc, restAnnotations)) {
      return null;
    }

    String controllerClassName = classDoc.typeName();
    Optional<DocRequestMappingAnnotation> requestMappingAnnotation = restAnnotations.requestMappingAnnotation();
    if (!requestMappingAnnotation.isPresent()) {
      throw new IllegalArgumentException("controller class " + controllerClassName
          + " is missing request mapping annotation");
    }
    List<String> uris = requestMappingAnnotation.get().getValue();
    if (uris.size() == 0) {
      throw new IllegalArgumentException("controller class "
          + controllerClassName
          + " is missing request mapping annotation's value (uri).");
    }
    for (String uri : uris) {
      DocController controller = new DocController(controllerClassName);

      SortedMap<String, DocMethod> generatedMethods = generateMethods(classDoc.methods());
      if (generatedMethods.isEmpty()) {
        throw new IllegalArgumentException("controller class "
            + controller.getName() + " doesn't have methods.");
      }
      controller.setMethods(generatedMethods);
      controller.setUri(uri);
      controller.setDescription(classDoc.commentText());

      controllers.add(controller);
    }
    return controllers;
  }

  private SortedMap<String, DocMethod> generateMethods(final MethodDoc[] methods)
          throws Exception {
    SortedMap<String, DocMethod> docMethods = new TreeMap<String, DocMethod>();

    for (MethodDoc methodDoc : methods) {
      try {
        List<AnnotationDesc> annotations = newArrayList();
        annotations.addAll(Arrays.asList(methodDoc.annotations()));
        annotations.addAll(paramAnnotations(methodDoc));
        RestAnnotations restAnnotations = annotationReader.read(annotations, Arrays.asList(methodDoc.tags()));

        // Does not handle methods without a RequestMapping annotation.
        if (restAnnotations.getAnnotation(RestDocConstants.DocAnnotationTypes.INTERNAL_METHOD) != null || !restAnnotations.requestMappingAnnotation().isPresent()) {
          continue;
        }

        // get all HTTP methods
        DocRequestMappingAnnotation requestMappingAnnotation = restAnnotations.requestMappingAnnotation().get();
        DocHttpMethod[] docHttpMethodArray = httpMethodDoc(requestMappingAnnotation.getMethod(), methodDoc, restAnnotations);

        for (String uri : requestMappingAnnotation.getValue()) {
          DocMethod docMethod = docMethods.get(uri);
          // If method with that uri already exist,
          // add the current httpMethod to the existing method.
          // There can be several httpMethods (GET, POST, DELETE) for each
          // uri.
          if (docMethod != null) {
            docMethod.addHttpMethods(docHttpMethodArray);
          } else {
            docMethod = new DocMethod(docHttpMethodArray);
            docMethod.setUri(uri);
          }
          docMethods.put(uri, docMethod);
        }
      }
      catch (Exception e) {
        logger.log(Level.SEVERE, "Failed processing method " + methodDoc.name());
        throw e;
      }
    }
    return docMethods;
  }

  private static List<AnnotationDesc> paramAnnotations(MethodDoc methodDoc) {
    List<AnnotationDesc> annotations = newArrayList();
    for (Parameter parameter : methodDoc.parameters()) {
      annotations.addAll(Arrays.asList(parameter.annotations()));
    }
    return annotations;
  }

  private DocHttpMethod[] httpMethodDoc(
          final List<String> methods,
          final MethodDoc methodDoc,
          final RestAnnotations annotations) throws Exception
  {
    if (methods.size() == 0) {
      return new DocHttpMethod[] {generateHttpMethod(methodDoc, "ALL", annotations)};
    }
    DocHttpMethod[] docHttpMethodArray = new DocHttpMethod[methods.size()];
    for (int i = 0; i < methods.size(); i++) {
      docHttpMethodArray[i] = generateHttpMethod(methodDoc, methods.get(i), annotations);
    }
    return docHttpMethodArray;
  }

  private DocHttpMethod generateHttpMethod(final MethodDoc methodDoc,
                                                  final String httpMethodName,
                                                  final RestAnnotations restAnnotations)
          throws Exception {
    if (StringUtils.isBlank(httpMethodName)) {
      throw new IllegalArgumentException("method " + methodDoc.name()
          +  " is missing the RequestMapping annotation's method parameter (eg GET).");
    }

    DocHttpMethod httpMethod = new DocHttpMethod(methodDoc.name(), httpMethodName);
    httpMethod.setDescription(methodDoc.commentText());
    httpMethod.setReturnDetails(generateReturnDetails(methodDoc));
    generateExamples(methodDoc, httpMethod, restAnnotations);
    httpMethod.setResponseStatuses(restAnnotations.responseStatusCodes());
    httpMethod.setHeaders(restAnnotations.requestMappingAnnotation().get().headers());

    if (restAnnotations.requestParamAnnotation() || restAnnotations.requestHeaderAnnotation() || restAnnotations.requestCommandAnnotation()) {
      httpMethod.setParams(generateRequestParams(methodDoc, restAnnotations));
    }

    return httpMethod;
  }

  private void generateExamples(final MethodDoc methodDoc, final DocHttpMethod httpMethod, final RestAnnotations annotations)
          throws Exception {
    DocJsonRequestExample requestExample = annotations.jsonRequestExample()
            .or(exampleGenerator.exampleRequest(methodDoc));
    httpMethod.setRequestExample(requestExample.generateJsonRequestBody());

    DocJsonResponseExample responseExample = annotations.jsonResponseExample()
            .or(exampleGenerator.exampleResponse(methodDoc));
    httpMethod.setResponseExample(responseExample.generateJsonResponseBody());
  }

  private static DocReturnDetails generateReturnDetails(final MethodDoc methodDoc) {
    DocReturnDetails returnDetails = new DocReturnDetails(methodDoc.returnType());
    Tag[] returnTags = methodDoc.tags("return");
    if (returnTags.length > 0) {
      returnDetails.setDescription(returnTags[0].text());
    }
    return returnDetails;
  }

  private List<DocParameter> generateRequestParams(final MethodDoc methodDoc, final RestAnnotations restAnnotations)
      throws Exception {
    final List<DocParameter> docParameters = newArrayList();
    final QueryParamGenerator generator = new QueryParamGenerator(restAnnotations);
    for (final Parameter param : methodDoc.parameters()) {
      for (final AnnotationDesc annotationDesc : param.annotations()) {
        if (annotationDesc.annotationType().qualifiedTypeName().equals(RequestParam.class.getTypeName())) {
          final DocRequestParamAnnotation paramAnnotation = new DocRequestParamAnnotation(annotationDesc);
          final DocParameter docParameter = generator.createParam(param, paramAnnotation, RestDocConstants.LOCATION_QUERY);
          docParameters.add(docParameter);
        }
        else if (annotationDesc.annotationType().qualifiedTypeName().equals(RequestHeader.class.getTypeName())) {
          final DocRequestParamAnnotation paramAnnotation = new DocRequestParamAnnotation(annotationDesc);
          final DocParameter docParameter = generator.createParam(param, paramAnnotation, RestDocConstants.LOCATION_HEADER);
          docParameters.add(docParameter);
        }
        else if (annotationDesc.annotationType().qualifiedTypeName().equals(DocumentCommand.class.getTypeName())) {
          final List<DocParameter> commandParams = generator.createCommandParams(param);
          docParameters.addAll(commandParams);
        }
      }
    }
    return docParameters;
  }
}
