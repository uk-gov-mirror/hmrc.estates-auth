/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.actions

import com.google.inject.Inject
import play.api.mvc._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import models.{AgentUser, IdentifierRequest, OrganisationUser}

import scala.concurrent.{ExecutionContext, Future}

class FakeIdentifierAction @Inject()(bodyParsers: BodyParsers.Default,
                                     affinityGroup: AffinityGroup,
                                     enrolments: Enrolments) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    affinityGroup match {
      case AffinityGroup.Agent =>
        block(IdentifierRequest(request, AgentUser("id", enrolments)))
      case _ =>
        block(IdentifierRequest(request, OrganisationUser("id", enrolments)))
    }
  }

  override def parser: BodyParser[AnyContent] =
    bodyParsers

  override protected def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
}
