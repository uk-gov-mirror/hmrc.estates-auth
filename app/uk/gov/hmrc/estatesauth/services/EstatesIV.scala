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

package uk.gov.hmrc.estatesauth.services

import com.google.inject.Inject
import play.api.Logger
import uk.gov.hmrc.auth.core.{BusinessKey, FailedRelationship, Relationship}
import uk.gov.hmrc.estatesauth.config.AppConfig
import uk.gov.hmrc.estatesauth.controllers.actions.EstatesAuthorisedFunctions
import uk.gov.hmrc.estatesauth.models.EstateAuthResponse
import uk.gov.hmrc.estatesauth.utils.Session
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class EstatesIV @Inject()(estatesAuth: EstatesAuthorisedFunctions, appConfig: AppConfig) {

  private val logger: Logger = Logger(getClass)

  def authenticate[A](utr: String,
                      onIVRelationshipExisting: Future[EstateAuthResponse],
                      onIVRelationshipNotExisting: Future[EstateAuthResponse]
                     )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EstateAuthResponse] = {

    val estateIVRelationship =
      Relationship(appConfig.relationshipName, Set(BusinessKey(appConfig.relationshipIdentifier, utr)))

    estatesAuth.authorised(estateIVRelationship) {
      onIVRelationshipExisting
    } recoverWith {
      case FailedRelationship(msg) =>
        logger.info(s"[authenticate][Session ID: ${Session.id(hc)}][UTR: $utr] Relationship does not exist in Estate IV for user due to $msg")
        onIVRelationshipNotExisting
    }
  }

}
