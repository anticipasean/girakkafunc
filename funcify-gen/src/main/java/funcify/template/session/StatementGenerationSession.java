package funcify.template.session;

import funcify.tool.container.SyncList;
import funcify.typedef.javatype.JavaType;
import java.util.Optional;

/**
 * @author smccarron
 * @created 2021-05-29
 */
public interface StatementGenerationSession<SWT, TD, MD, CD, SD, ED> extends
                                                                     ExpressionGenerationSession<SWT, TD, MD, CD, SD, ED> {

    SyncList<SD> getStatementsForCodeBlock(final CD codeBlockDef);

    default Optional<SD> getFirstStatementForCodeBlock(final CD codeBlockDef) {
        return getStatementsForCodeBlock(codeBlockDef).first();
    }

    default Optional<SD> getLastStatementForCodeBlock(final CD codeBlockDef) {
        return getStatementsForCodeBlock(codeBlockDef).last();
    }

    SD assignmentStatement(final JavaType assigneeType,
                           final String assigneeName,
                           final SyncList<ED> expressions);


    SD returnStatement(final SyncList<ED> expression);

}