package org.uengine.msa;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.metaworks.WebFieldDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.uengine.uml.ClassDiagram;
import org.uengine.uml.model.Attribute;
import org.uengine.uml.model.ClassDefinition;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.github.javaparser.ast.type.PrimitiveType.intType;

/**
 * Created by uengine on 2017. 10. 5..
 */
@Configuration
@ComponentScan
@RestController
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Application {

    public static final String GITLAB_SRC_ROOT_PATTERN = "/raw/master";
    @Autowired
    private Environment environment;

    private String getGitLabRelativeTemplateUrl(String sourceUrl, String templateFileName){
        int rootIndex = sourceUrl.indexOf(GITLAB_SRC_ROOT_PATTERN);
        String root = sourceUrl.substring(0, rootIndex + GITLAB_SRC_ROOT_PATTERN.length());
        String url = root + "/template/file/" + templateFileName;

        return url;
    }

//    @Value("${template.entity.url}")
//    private String entityTemplateUrl;
//
//    @Value("${template.repository.url}")
//    private String repositoryTemplateUrl;

    //Template entityClassTemplate;
    Template getEntityTemplate(String sourceUrl){

      //  if(entityClassTemplate!=null) return entityClassTemplate;


        Template entityClassTemplate = getSourceTemplate(getGitLabRelativeTemplateUrl(sourceUrl, "Entity.java"));

        return entityClassTemplate;
    }

    //Template repositoryClassTemplate;
    Template getRepositoryTemplate(String sourceUrl){

        //if(repositoryClassTemplate!=null) return repositoryClassTemplate;

        Template repositoryClassTemplate = getSourceTemplate(getGitLabRelativeTemplateUrl(sourceUrl, "Repository.java"));

        return repositoryClassTemplate;
    }

    Template getSourceTemplate(String sourceUrl){

        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            URL url = new URL(sourceUrl);
            copyStream(url.openStream(), bao);

            String source = bao.toString();

            return Mustache.compiler().nullValue("").compile(source);

        } catch (Exception e) {
            throw new RuntimeException("unable to get template from " + sourceUrl, e);
        }
    }

    @RequestMapping(value = "/java", method = RequestMethod.POST)
    public GenerationResult java(@RequestParam("sourceUrl") String sourceUrl, @RequestBody ClassDefinition classDefinition) throws Exception {

        CompilationUnit cu = null;
        String sourceCode = null;

        GenerationResult generationResult = new GenerationResult();
        generationResult.setClassDefinition(classDefinition);

        try {
            if (sourceUrl != null) { //in case of update class (must be a round-trip engineering)
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                URL url = new URL(sourceUrl);
                copyStream(url.openStream(), bao);

                String source = bao.toString();

                cu = JavaParser.parse(source);
            }
        }catch (FileNotFoundException e){

            //case of create new source code
        }

        if(cu==null){
            Map<String, Object> templateData = new HashMap<String, Object>();
            templateData.put("templateSpecificData", classDefinition);

            complementClassDefinition(classDefinition);

            sourceCode = getEntityTemplate(sourceUrl).execute(templateData);

            Action entityClassCreationAction = new Action();
            entityClassCreationAction.setAction("create");
            entityClassCreationAction.setFileName(classDefinition.getName() + ".java");
            entityClassCreationAction.setSourceCode(sourceCode);

            generationResult.getActions().add(entityClassCreationAction);

            Action repositoryClassCreationAction = new Action();
            repositoryClassCreationAction.setAction("create");
            repositoryClassCreationAction.setFileName(classDefinition.getName() + "Repository.java");
            repositoryClassCreationAction.setSourceCode(getRepositoryTemplate(sourceUrl).execute(templateData));

            generationResult.getActions().add(repositoryClassCreationAction);


        }else {


           // System.out.println(cu.toString());

            Set<String> fieldManifest = new HashSet<>();
            Set<String> modelFieldManifest = new HashSet<>();

            NodeList<TypeDeclaration<?>> types = cu.getTypes();


            Action entityClassCreationAction = new Action();
            entityClassCreationAction.setFileName(classDefinition.getName() + ".java");


            if(types.size()>0) {
                TypeDeclaration type = types.get(0);

                // if the class name has been changed, file name must be changed as well

                if (!type.getName().getIdentifier().equals(classDefinition.getName())) {
                    entityClassCreationAction.setAction("move");
                    entityClassCreationAction.setPreviousFileName(classDefinition.getJavaClassName() + ".java");
                    type.getName().setIdentifier(classDefinition.getName());
                } else {
                    entityClassCreationAction.setAction("update");
                }

                if (Optional.empty().equals(type.getAnnotationByName("Entity"))) {
                    return null;
                }

                NodeList<BodyDeclaration<?>> members = type.getMembers();

                //TODO:  problematic: super class setting is not bi-directional synchronizable.
                if(classDefinition.getSuperClasses()!=null && classDefinition.getSuperClasses().size() > 0){

                    String superClassName = (classDefinition.getSuperClasses().get(0));
                    ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration)type;
                    classOrInterfaceDeclaration.setExtendedTypes(new NodeList<>()); //remove super class info first.

                    classOrInterfaceDeclaration.addExtendedType(superClassName);
                }

                for (BodyDeclaration<?> member : members) {
                    if (member instanceof FieldDeclaration) {
                        FieldDeclaration field = (FieldDeclaration) member;

                        if (field.getVariables().size() > 0)
                            fieldManifest.add(field.getVariable(0).getName().getIdentifier());
                        //fieldManifest.add(field.getMetaModel().getMetaModelFieldName().toString());
                    }
                }

                //update to source code
                for (WebFieldDescriptor fieldDescriptor : classDefinition.getFieldDescriptors()) {

                    FieldDeclaration fieldDeclaration ;
                    VariableDeclarator variableDeclarator;

                    if (fieldManifest.contains(fieldDescriptor.getName())) {

                        fieldDeclaration = (FieldDeclaration) type.getFieldByName(fieldDescriptor.getName()).get();
                        variableDeclarator = fieldDeclaration.getVariable(0);

                    }else {
                        fieldDeclaration = new FieldDeclaration();

                        variableDeclarator = new VariableDeclarator();
                        variableDeclarator.setName(new SimpleName());
                        variableDeclarator.setType(fieldDescriptor.getClassName());
                        variableDeclarator.getName().setIdentifier(fieldDescriptor.getName());

                        fieldDeclaration.getVariables().add(variableDeclarator);

                        type.addMember(fieldDeclaration);

                        fieldDeclaration.createGetter();
                        fieldDeclaration.createSetter();

                    }

                    variableDeclarator.setType(fieldDescriptor.getClassName());

                    //remove managing annotations
                    for(String existingAnnotation: new String[]{"OneToOne", "OneToMany", "ManyToMany", "ManyToOne", "Id", "GeneratedValue"}){
                        List<Node> toRemove = new ArrayList<Node>();

                        for(Node node : fieldDeclaration.getChildNodes()){

                            if(node instanceof AnnotationExpr && existingAnnotation.equals(((AnnotationExpr) node).getNameAsString()))
                                toRemove.add(node);
                        }

                        for(Node node: toRemove) {
                            fieldDeclaration.remove(node);
                        }
                    }



                    //add relation annotation
                    String relationAnnotation = (String) fieldDescriptor.getAttribute("relationAnnotation");
                    if(relationAnnotation != null) {
                        fieldDeclaration.addAnnotation(relationAnnotation);
                        NormalAnnotationExpr annotationExpr = (NormalAnnotationExpr) fieldDeclaration.getAnnotationByName(relationAnnotation).get();

                        String relationAnnotationMappedBy = (String) fieldDescriptor.getAttribute("relationAnnotation.mappedBy");

                        if(relationAnnotationMappedBy != null){
                            ((NormalAnnotationExpr)fieldDeclaration.getAnnotationByName(relationAnnotation).get()).addPair("mappedBy", "\"" + relationAnnotationMappedBy + "\"");
                        }

                    }

                    //add id and keygen annotation
                    if("true".equals(fieldDescriptor.getAttribute("isKey"))){
                        fieldDeclaration.addAnnotation("Id");
                        fieldDeclaration.addAnnotation("GeneratedValue");
                    }

                    modelFieldManifest.add(fieldDescriptor.getName());
                }

                //update to model
                for (BodyDeclaration<?> member : members) {
                    if (member instanceof FieldDeclaration) {
                        FieldDeclaration field = (FieldDeclaration) member;

                        if (field.getVariables().size() > 0) {
                            VariableDeclarator variableDeclarator = field.getVariables().get(0);
                            if (!modelFieldManifest.contains(variableDeclarator.getName().getIdentifier())) {

                                Attribute attribute = new Attribute();
                                attribute.setName(variableDeclarator.getName().getIdentifier());
                                attribute.setClassName(variableDeclarator.getType().getElementType().asString());

                                classDefinition.addFieldDescriptor(attribute);

                            }
                        }

                    }
                }

            }

            sourceCode = cu.toString();
            entityClassCreationAction.setSourceCode(sourceCode);

            generationResult.getActions().add(entityClassCreationAction);

        }

        classDefinition.setSourceCode(sourceCode);
        classDefinition.setJavaClassName(classDefinition.getName());

        return generationResult;
    }

    private void complementClassDefinition(@RequestBody ClassDefinition classDefinition) {
        WebFieldDescriptor keyField = null;
        for(WebFieldDescriptor attribute : classDefinition.getFieldDescriptors()){
            if("true".equals(attribute.getAttribute("isKey"))) keyField = attribute;

            if(attribute.getAttributes()==null)
                attribute.setAttributes(new Properties());

            attribute.getAttributes().put("nameForSetterGetter", attribute.getName().substring(0, 1).toUpperCase() + attribute.getName().substring(1));
        }

        if(keyField==null && classDefinition.getFieldDescriptors().length > 0 && (classDefinition.getSuperClasses()==null || classDefinition.getSuperClasses().size()==0)){
            keyField = classDefinition.getFieldDescriptors()[0];
            keyField.getAttributes().put("isKey", "true");
        }

        classDefinition.setKeyFieldDescriptor(keyField);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return createTypedJsonObjectMapper();
    }

    public static ObjectMapper createTypedJsonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // ignore null
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT); // ignore zero and false when it is int or boolean
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "_type");
        return objectMapper;
    }


    //for reverse engineering

    @RequestMapping(value = "/java", method = RequestMethod.GET)
    public ClassDiagram java(@RequestParam("sourceUrl") String sourceUrl) throws Exception {
        return null;
    }

    static public void copyStream(InputStream sourceInputStream, OutputStream targetOutputStream) throws Exception {
        int length = 1024;
        byte[] bytes = new byte[length];
        int c;
        int total_bytes = 0;

        while ((c = sourceInputStream.read(bytes)) != -1) {
            total_bytes += c;
            targetOutputStream.write(bytes, 0, c);
        }

        if (sourceInputStream != null) try {
            sourceInputStream.close();
        } catch (Exception e) {
        }
        if (targetOutputStream != null) try {
            targetOutputStream.close();
        } catch (Exception e) {
        }
    }

    @RequestMapping("/")
    public String home() throws Exception {
        return homeService.getHome();
    }

    private static Log logger = LogFactory.getLog(Application.class);

    public static void main(String[] args) throws FileNotFoundException {

        SpringApplication.run(Application.class, args);

//        FileInputStream in = new FileInputStream("/Users/uengine/java-reverse/src/main/java/org/uengine/msa/Application.java");
//
//        // parse the file
//        CompilationUnit cu = JavaParser.parse(in);
//
//        // prints the resulting compilation unit to default system output
//        System.out.println(cu.toString());
//
//        changeMethods(cu);
//
//        System.out.println(cu.toString());



    }



    private static void changeMethods(CompilationUnit cu) {
        // Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    changeMethod(method);
                }
            }
        }
    }

    private static void changeMethod(MethodDeclaration n) {
        // change the name of the method to upper case
        n.setName(n.getNameAsString().toUpperCase());

        // create the new parameter
        n.addParameter(intType(), "value");
    }

}