package graph;

import graph.EditableGraphTest.SimpleElementalAnalyzerへの利用を確認する;
import graph.EditableGraphTest.グラフ頂点や辺の登録及び削除;
import graph.IdentifiedGraphTest.EditableGraphへの委譲;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EdgeTest.class, EditableGraphTest.class, EditableGraphへの委譲.class, IdentifiedGraphTest.class,
    SimpleElementalAnalyzerへの利用を確認する.class, グラフ頂点や辺の登録及び削除.class })
public class AllTests {

}
