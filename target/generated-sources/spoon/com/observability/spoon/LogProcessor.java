package com.observability.spoon;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
public class LogProcessor extends AbstractProcessor<CtMethod<?>> {
    @Override
    public boolean isToBeProcessed(CtMethod<?> candidate) {
        // Process only public methods in ProductRepository
        return ((candidate.getParent() instanceof CtClass) && ((CtClass<?>) (candidate.getParent())).getQualifiedName().equals("com.observability.repository.ProductRepository")) && candidate.isPublic();
    }

    @Override
    public void process(CtMethod<?> method) {
        String methodName = method.getSimpleName();
        String type = ((methodName.equals("save") || methodName.equals("update")) || methodName.equals("delete")) ? "WRITE" : "READ";
        // Construct code to extract data based on parameters
        String dataExtraction = "\"{}\"";// Default empty JSON object

        if (method.getParameters().size() > 0) {
            String paramName = method.getParameters().get(0).getSimpleName();
            String paramType = method.getParameters().get(0).getType().getSimpleName();
            if (paramType.equals("Product")) {
                // Extract ID and Price
                dataExtraction = ((("String.format(java.util.Locale.US, \"{\\\"id\\\": \\\"%s\\\", \\\"price\\\": %.2f}\", " + paramName) + ".getId(), ") + paramName) + ".getPrice())";
            } else if (paramType.equals("String")) {
                // Extract ID only
                dataExtraction = ("String.format(\"{\\\"id\\\": \\\"%s\\\"}\", " + paramName) + ")";
            }
        }
        // JSON Structure: { "user": "...", "action": "...", "type": "...", "data": ...
        // }
        // JSON Structure: { "user": "...", "action": "...", "type": "...", "data": ...
        // }
        String startSnippet = "String logUser = com.observability.util.UserContext.getUserId();";
        String logSnippet = (((((("org.slf4j.LoggerFactory.getLogger(com.observability.repository.ProductRepository.class)" + ".info(String.format(java.util.Locale.US, \"{\\\"user\\\": \\\"%s\\\", \\\"action\\\": \\\"") + methodName) + "\\\", \\\"type\\\": \\\"") + type) + "\\\", \\\"data\\\": %s }\", logUser, ") + dataExtraction) + "));";
        CtCodeSnippetStatement startStmt = getFactory().Code().createCodeSnippetStatement(startSnippet);
        CtCodeSnippetStatement logStmt = getFactory().Code().createCodeSnippetStatement(logSnippet);
        if (method.getBody() != null) {
            method.getBody().insertBegin(logStmt);
            method.getBody().insertBegin(startStmt);
        }
    }
}