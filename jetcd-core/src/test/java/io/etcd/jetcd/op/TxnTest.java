/**
 * Copyright 2017 The jetcd authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.etcd.jetcd.op;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.options.PutOption;
import java.util.concurrent.ExecutionException;
import org.junit.Test;

public class TxnTest {

  final static Cmp CMP = new Cmp(ByteSequence.from("key", UTF_8), Cmp.Op.GREATER,
      CmpTarget.value(ByteSequence.from("value", UTF_8)));
  final static Op OP = Op
      .put(ByteSequence.from("key2", UTF_8), ByteSequence.from("value2", UTF_8), PutOption.DEFAULT);

  @Test
  @SuppressWarnings("FutureReturnValueIgnored") // CompletableFuture is null
  public void testIfs() throws InterruptedException, ExecutionException {
    TxnImpl.newTxn((t) -> null).If(CMP).If(CMP).commit();
  }

  @Test
  @SuppressWarnings("FutureReturnValueIgnored") // CompletableFuture is null
  public void testThens() throws InterruptedException, ExecutionException {
    TxnImpl.newTxn((t) -> null).Then(OP).Then(OP).commit();
  }

  @Test
  @SuppressWarnings("FutureReturnValueIgnored") // CompletableFuture is null
  public void testElses() throws InterruptedException, ExecutionException {
    TxnImpl.newTxn((t) -> null).Else(OP).Else(OP).commit();
  }

  @Test
  public void testIfAfterThen() {
    assertThatThrownBy(() -> TxnImpl.newTxn((t) -> null).Then(OP).If(CMP).commit().get())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot call If after Then!");
  }

  @Test
  public void testIfAfterElse() {
    assertThatThrownBy(() -> TxnImpl.newTxn((t) -> null).Else(OP).If(CMP).commit().get())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot call If after Else!");
  }

  @Test
  public void testThenAfterElse() {
    assertThatThrownBy(() -> TxnImpl.newTxn((t) -> null).Else(OP).Then(OP).commit().get())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot call Then after Else!");
  }
}