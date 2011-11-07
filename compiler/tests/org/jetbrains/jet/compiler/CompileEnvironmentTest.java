package org.jetbrains.jet.compiler;

import jet.modules.IModuleBuilder;
import jet.modules.IModuleSetBuilder;
import junit.framework.TestCase;
import org.jetbrains.jet.codegen.ClassFileFactory;
import org.jetbrains.jet.parsing.JetParsingTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author yole
 */
public class CompileEnvironmentTest extends TestCase {
    private CompileEnvironment environment;

    protected void setUp() throws Exception {
        super.setUp();
        environment = new CompileEnvironment();
    }

    @Override
    protected void tearDown() throws Exception {
        environment.dispose();
        super.tearDown();
    }

    public void testSmoke() throws IOException {
        final File activeRtJar = CompileEnvironment.findRtJar(true);
        environment.setJavaRuntime(activeRtJar);
        environment.initializeKotlinRuntime();
        final String testDataDir = JetParsingTest.getTestDataDir() + "/compiler/smoke/";
        final IModuleSetBuilder setBuilder = environment.loadModuleScript(testDataDir + "Smoke.kts");
        assertEquals(1, setBuilder.getModules().size());
        final IModuleBuilder moduleBuilder = setBuilder.getModules().get(0);
        final ClassFileFactory factory = environment.compileModule(moduleBuilder, testDataDir);
        assertNotNull(factory);
        assertNotNull(factory.asBytes("Smoke/namespace.class"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompileEnvironment.writeToJar(factory, baos, null, false);
        JarInputStream is = new JarInputStream(new ByteArrayInputStream(baos.toByteArray()));
        final List<String> entries = listEntries(is);
        assertTrue(entries.contains("Smoke/namespace.class"));
    }

    private List<String> listEntries(JarInputStream is) throws IOException {
        List<String> entries = new ArrayList<String>();
        while (true) {
            final JarEntry jarEntry = is.getNextJarEntry();
            if (jarEntry == null) {
                break;
            }
            entries.add(jarEntry.getName());
        }
        return entries;
    }
}