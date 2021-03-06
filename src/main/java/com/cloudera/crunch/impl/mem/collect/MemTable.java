/**
 * Copyright (c) 2012, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.crunch.impl.mem.collect;

import java.util.List;

import com.cloudera.crunch.GroupingOptions;
import com.cloudera.crunch.PGroupedTable;
import com.cloudera.crunch.PTable;
import com.cloudera.crunch.Pair;
import com.cloudera.crunch.Target;
import com.cloudera.crunch.type.PTableType;
import com.cloudera.crunch.type.PType;
import com.google.common.collect.Lists;

public class MemTable<K, V> extends MemCollection<Pair<K, V>> implements PTable<K, V> {

  private PTableType<K, V> ptype;
  
  public MemTable(Iterable<Pair<K, V>> collect) {
    this(collect, null, null);
  }
  
  public MemTable(Iterable<Pair<K, V>> collect, PTableType<K, V> ptype, String name) {
    super(collect, ptype, name);
    this.ptype = ptype;
  }
  
  @Override
  public PTable<K, V> union(PTable<K, V>... others) {
    List<Pair<K, V>> values = Lists.newArrayList();
    values.addAll(getCollection());
    for (PTable<K, V> ptable : others) {
      for (Pair<K, V> p : ptable.materialize()) {
        values.add(p);
      }
    }
    return new MemTable<K, V>(values, others[0].getPTableType(), null);
  }

  @Override
  public PGroupedTable<K, V> groupByKey() {
    return new MemGroupedTable<K, V>(this);
  }

  @Override
  public PGroupedTable<K, V> groupByKey(int numPartitions) {
    return groupByKey();
  }

  @Override
  public PGroupedTable<K, V> groupByKey(GroupingOptions options) {
    //TODO: make this work w/the grouping options
    return groupByKey();
  }

  @Override
  public PTable<K, V> write(Target target) {
    super.write(target);
    return this;
  }
  
  @Override
  public PTableType<K, V> getPTableType() {
    return ptype;
  }

  @Override
  public PType<K> getKeyType() {
    if (ptype != null) {
      return ptype.getKeyType();
    }
    return null;
  }

  @Override
  public PType<V> getValueType() {
    if (ptype != null) {
      return ptype.getValueType();
    }
    return null;
  }
}
