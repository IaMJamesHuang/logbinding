package com.james.logbinding_processor;

import com.google.auto.service.AutoService;
import com.james.logbinding_annotations.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by James on 2018/12/16.
 */
@AutoService(Processor.class)
public class LogBindingProcessor extends AbstractProcessor {

    private Elements elementsUtils;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(BindView.class.getCanonicalName());
        return set;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeElement, List<BindingField>> map = getTargetMap(roundEnvironment);
        generateJavaFile(map.entrySet());
        return false;
    }

    private Map<TypeElement, List<BindingField>> getTargetMap(RoundEnvironment roundEnvironment) {
        Map<TypeElement, List<BindingField>> targetMap = new HashMap<>();

        Set<? extends Element> bindElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : bindElements) {
            String fieldName = element.getSimpleName().toString();
            TypeMirror type = element.asType();
            int id = element.getAnnotation(BindView.class).id();
            String values = element.getAnnotation(BindView.class).value();
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            List<BindingField> list = targetMap.computeIfAbsent(typeElement, value -> new ArrayList<>());
            list.add(new BindingField(type, id, values, fieldName));
        }
        return targetMap;
    }

    private void generateJavaFile(Set<Map.Entry<TypeElement, List<BindingField>>> entries) {
        for (Map.Entry<TypeElement, List<BindingField>> entry : entries) {
            TypeElement typeElement = entry.getKey();
            List<BindingField> list = entry.getValue();
            if (list == null || list.size() == 0) {
                continue;
            }

            String packageName = elementsUtils.getPackageOf(typeElement).getQualifiedName().toString();
            String className = typeElement.getQualifiedName().toString().substring(packageName.length() + 1);
            String realClassName = className + "_LogBinding";

            MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess(className), "target");

            for (BindingField bindingField : list) {
                String packageNameString = bindingField.getType().toString();
                ClassName viewClass = ClassName.bestGuess(packageNameString);
                methodBuilder.addStatement("target.$L = ($T)target.findViewById($L)", bindingField.getFieldName(), viewClass, bindingField.getId());
                methodBuilder.addStatement("target.textView.setOnTouchListener(new android.view.View.OnTouchListener() {" +
                        "public boolean onTouch(android.view.View v, android.view.MotionEvent event) {" +
                        "String tag = \"LogBinding:$L\";" +
                        "switch (event.getAction()) {" +
                        "case android.view.MotionEvent.ACTION_DOWN:android.util.Log.e(tag, \"onTouch: down\");break;\r\n" +
                        "case android.view.MotionEvent.ACTION_UP:android.util.Log.e(tag, \"onTouch: up\");break;\r\n" +
                        "case android.view.MotionEvent.ACTION_MOVE:android.util.Log.e(tag, \"onTouch: move\");break;\r\n" +
                        "case android.view.MotionEvent.ACTION_CANCEL:android.util.Log.e(tag, \"onTouch: cancel\");break;\r\n}" +
                        "return false;}})", bindingField.getFieldName());
            }

            TypeSpec typeBuilder = TypeSpec.classBuilder(realClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, typeBuilder)
                    .addFileComment("Generated code from Log Binding. Do not modify!")
                    .build();

            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
