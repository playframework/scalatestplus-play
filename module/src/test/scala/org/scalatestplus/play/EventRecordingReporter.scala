/*
 * Copyright 2001-2022 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalatestplus.play

import org.scalatest.events._
import org.scalatest.Reporter

class EventRecordingReporter extends Reporter {
  private var eventList: List[Event] = List()
  def eventsReceived                 = eventList.reverse
  def testSucceededEventsReceived: List[TestSucceeded] = {
    eventsReceived
      .filter {
        case event: TestSucceeded => true
        case _                    => false
      }
      .map {
        case event: TestSucceeded => event
        case _                    => throw new RuntimeException("should never happen")
      }
  }
  def testStartingEventsReceived: List[TestStarting] = {
    eventsReceived
      .filter {
        case event: TestStarting => true
        case _                   => false
      }
      .map {
        case event: TestStarting => event
        case _                   => throw new RuntimeException("should never happen")
      }
  }
  // Why doesn't this work:
  // for (event: TestSucceeded <- eventsReceived) yield event
  def infoProvidedEventsReceived: List[InfoProvided] = {
    eventsReceived
      .filter {
        case event: InfoProvided => true
        case _                   => false
      }
      .map {
        case event: InfoProvided => event
        case _                   => throw new RuntimeException("should never happen")
      }
  }
  def noteProvidedEventsReceived: List[NoteProvided] = {
    eventsReceived
      .filter {
        case event: NoteProvided => true
        case _                   => false
      }
      .map {
        case event: NoteProvided => event
        case _                   => throw new RuntimeException("should never happen")
      }
  }
  def alertProvidedEventsReceived: List[AlertProvided] = {
    eventsReceived
      .filter {
        case event: AlertProvided => true
        case _                    => false
      }
      .map {
        case event: AlertProvided => event
        case _                    => throw new RuntimeException("should never happen")
      }
  }
  def markupProvidedEventsReceived: List[MarkupProvided] = {
    eventsReceived
      .filter {
        case event: MarkupProvided => true
        case _                     => false
      }
      .map {
        case event: MarkupProvided => event
        case _                     => throw new RuntimeException("should never happen")
      }
  }
  def scopeOpenedEventsReceived: List[ScopeOpened] = {
    eventsReceived
      .filter {
        case event: ScopeOpened => true
        case _                  => false
      }
      .map {
        case event: ScopeOpened => event
        case _                  => throw new RuntimeException("should never happen")
      }
  }
  def scopeClosedEventsReceived: List[ScopeClosed] = {
    eventsReceived
      .filter {
        case event: ScopeClosed => true
        case _                  => false
      }
      .map {
        case event: ScopeClosed => event
        case _                  => throw new RuntimeException("should never happen")
      }
  }
  def scopePendingEventsReceived: List[ScopePending] = {
    eventsReceived
      .filter {
        case event: ScopePending => true
        case _                   => false
      }
      .map {
        case event: ScopePending => event
        case _                   => throw new RuntimeException("should never happen")
      }
  }
  def testPendingEventsReceived: List[TestPending] = {
    eventsReceived
      .filter {
        case event: TestPending => true
        case _                  => false
      }
      .map {
        case event: TestPending => event
        case _                  => throw new RuntimeException("should never happen")
      }
  }
  def testCanceledEventsReceived: List[TestCanceled] = {
    eventsReceived
      .filter {
        case event: TestCanceled => true
        case _                   => false
      }
      .map {
        case event: TestCanceled => event
        case _                   => throw new RuntimeException("should never happen")
      }
  }
  def testFailedEventsReceived: List[TestFailed] = {
    eventsReceived
      .filter {
        case event: TestFailed => true
        case _                 => false
      }
      .map {
        case event: TestFailed => event
        case _                 => throw new RuntimeException("should never happen")
      }
  }
  def testIgnoredEventsReceived: List[TestIgnored] = {
    eventsReceived
      .filter {
        case event: TestIgnored => true
        case _                  => false
      }
      .map {
        case event: TestIgnored => event
        case _                  => throw new RuntimeException("should never happen")
      }
  }
  def suiteStartingEventsReceived: List[SuiteStarting] = {
    eventsReceived
      .filter {
        case event: SuiteStarting => true
        case _                    => false
      }
      .map {
        case event: SuiteStarting => event
        case _                    => throw new RuntimeException("should never happen")
      }
  }
  def suiteCompletedEventsReceived: List[SuiteCompleted] = {
    eventsReceived
      .filter {
        case event: SuiteCompleted => true
        case _                     => false
      }
      .map {
        case event: SuiteCompleted => event
        case _                     => throw new RuntimeException("should never happen")
      }
  }
  def suiteAbortedEventsReceived: List[SuiteAborted] = {
    eventsReceived
      .filter {
        case event: SuiteAborted => true
        case _                   => false
      }
      .map {
        case event: SuiteAborted => event
        case _                   => throw new RuntimeException("should never happen")
      }
  }
  def apply(event: Event): Unit = {
    eventList ::= event
  }
}
