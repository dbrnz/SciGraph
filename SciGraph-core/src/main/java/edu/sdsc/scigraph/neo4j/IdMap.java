/**
 * Copyright (C) 2014 The SciGraph authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sdsc.scigraph.neo4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ForwardingConcurrentMap;

/***
 * A map of external unique keys to internal Neo4j IDs for keeping track of these mappings during a
 * bulk load.
 *
 * TODO: This could be switched to MapDB if this structure needs to persist.
 *
 */
@ThreadSafe
public class IdMap extends ForwardingConcurrentMap<String, Long> {

  private static final int INITIAL_CAPACITY = 500_000;
  
  private final ConcurrentHashMap<String, Long> delegate;
  private final AtomicLong idCounter = new AtomicLong();

  public IdMap() {
    this(INITIAL_CAPACITY);
  }

  public IdMap(int initialCapacity) {
    delegate = new ConcurrentHashMap<String, Long>(initialCapacity);
  }

  @Override
  protected ConcurrentMap<String, Long> delegate() {
    return delegate();
  }

  @Override
  public Long get(Object key) {
    delegate.putIfAbsent((String) key, idCounter.getAndIncrement());
    return delegate.get(key);
  }

}