package org.apache.hadoop.hive.ql.io.parquet.serde;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.hadoop.hive.ql.io.parquet.serde.primitive.ParquetPrimitiveInspectorFactory;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.junit.Test;

/**
 *
 * @author Rémy Pecqueur <r.pecqueur@criteo.com>
 */
public class TestDeepParquetHiveMapInspector extends TestCase {

  private DeepParquetHiveMapInspector inspector;

  @Override
  public void setUp() {
    inspector = new DeepParquetHiveMapInspector(ParquetPrimitiveInspectorFactory.parquetShortInspector,
            PrimitiveObjectInspectorFactory.javaIntObjectInspector);
  }

  @Test
  public void testNullMap() {
    assertNull("Should be null", inspector.getMapValueElement(null, new ShortWritable((short) 0)));
  }

  @Test
  public void testNullContainer() {
    final ArrayWritable map = new ArrayWritable(ArrayWritable.class, null);
    assertNull("Should be null", inspector.getMapValueElement(map, new ShortWritable((short) 0)));
  }

  @Test
  public void testEmptyContainer() {
    final ArrayWritable map = new ArrayWritable(ArrayWritable.class, new ArrayWritable[0]);
    assertNull("Should be null", inspector.getMapValueElement(map, new ShortWritable((short) 0)));
  }

  @Test
  public void testRegularMap() {
    final Writable[] entry1 = new Writable[]{new IntWritable(0), new IntWritable(1)};
    final Writable[] entry2 = new Writable[]{new IntWritable(2), new IntWritable(3)};

    final ArrayWritable internalMap = new ArrayWritable(ArrayWritable.class, new Writable[]{
      new ArrayWritable(Writable.class, entry1), new ArrayWritable(Writable.class, entry2)});

    final ArrayWritable map = new ArrayWritable(ArrayWritable.class, new Writable[]{internalMap});

    assertEquals("Wrong result of inspection", new IntWritable(1), inspector.getMapValueElement(map, new IntWritable(0)));
    assertEquals("Wrong result of inspection", new IntWritable(3), inspector.getMapValueElement(map, new IntWritable(2)));
    assertEquals("Wrong result of inspection", new IntWritable(1), inspector.getMapValueElement(map, new ShortWritable((short) 0)));
    assertEquals("Wrong result of inspection", new IntWritable(3), inspector.getMapValueElement(map, new ShortWritable((short) 2)));
  }

  @Test
  public void testHashMap() {
    final Map<Writable, Writable> map = new HashMap<Writable, Writable>();
    map.put(new IntWritable(0), new IntWritable(1));
    map.put(new IntWritable(2), new IntWritable(3));
    map.put(new IntWritable(4), new IntWritable(5));
    map.put(new IntWritable(6), new IntWritable(7));


    assertEquals("Wrong result of inspection", new IntWritable(1), inspector.getMapValueElement(map, new IntWritable(0)));
    assertEquals("Wrong result of inspection", new IntWritable(3), inspector.getMapValueElement(map, new IntWritable(2)));
    assertEquals("Wrong result of inspection", new IntWritable(5), inspector.getMapValueElement(map, new IntWritable(4)));
    assertEquals("Wrong result of inspection", new IntWritable(7), inspector.getMapValueElement(map, new IntWritable(6)));
    assertEquals("Wrong result of inspection", new IntWritable(1), inspector.getMapValueElement(map, new ShortWritable((short) 0)));
    assertEquals("Wrong result of inspection", new IntWritable(3), inspector.getMapValueElement(map, new ShortWritable((short) 2)));
    assertEquals("Wrong result of inspection", new IntWritable(5), inspector.getMapValueElement(map, new ShortWritable((short) 4)));
    assertEquals("Wrong result of inspection", new IntWritable(7), inspector.getMapValueElement(map, new ShortWritable((short) 6)));
  }
}
