package coder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import coder.EdgeCoderTest.エスケープ処理;
import coder.EdgeCoderTest.正常系;

@RunWith(Suite.class)
@SuiteClasses({ AttributeCoderTest.class, EdgeCoderTest.class, GraphCoderTest.class, SimpleAttributeTest.class,
    VertexCoderTest.class, エスケープ処理.class, 正常系.class })
public class AllTests {

}
