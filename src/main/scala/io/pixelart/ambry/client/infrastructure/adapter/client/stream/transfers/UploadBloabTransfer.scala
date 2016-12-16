package io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Flow
import io.pixelart.ambry.client.domain.model.{ AmbryPostFileResponse, UploadBlobRequestData, AmbryUri }
import io.pixelart.ambry.client.infrastructure.adapter.client.stream.transfers.UploadBloabTransfer.{ UploadBloabRequestData }
import io.pixelart.ambry.client.infrastructure.adapter.client.{ AmbryHttpClientResponseHandler, Execution }

object UploadBloabTransfer {
  case class UploadBloabRequestData(ambryUri: AmbryUri, uploadData: UploadBlobRequestData)
}
trait UploadBloabTransfer extends AmbryHttpClientResponseHandler {
  self: Execution =>

  //  import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
  //  import io.outofaxis.pixelart.cms.router.infrastructure.player.xmp.translator.XMPPlayerFormatter._

  def flowAuthenticate: Flow[UploadBloabRequestData, AmbryPostFileResponse, NotUsed] =
    Flow[UploadBloabRequestData].mapAsync(1) { data =>
      val httpReq = httpRequests.uploadBlobHttpRequest(data.ambryUri, data.uploadData)

      val unmarshalFunc = (r: HttpResponse) => Unmarshal(r).to[AmbryPostFileResponse]

      requestsExecutor.executeRequest(httpReq, unmarshalFunc)
    }
}
