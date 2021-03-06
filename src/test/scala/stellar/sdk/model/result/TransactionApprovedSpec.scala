package stellar.sdk.model.result

import org.specs2.mutable.Specification
import stellar.sdk.model.response.TransactionApproved
import stellar.sdk.util.ByteArrays
import stellar.sdk.ArbitraryInput
import stellar.sdk.model.NativeAmount

class TransactionApprovedSpec extends Specification with ArbitraryInput {

  "an approved transaction result" should {
    "provide direct access to the fee charged" >> {
      val resultXDR = ByteArrays.base64(TransactionSuccess(NativeAmount(982346), Seq(PaymentSuccess)).encode)
      TransactionApproved("", 1, "", resultXDR, "").feeCharged mustEqual NativeAmount(982346)
    }

    "provide direct access to the operation results" >> prop { opResults: Seq[OperationResult] =>
      val resultXDR = ByteArrays.base64(TransactionSuccess(NativeAmount(100), opResults).encode)
      TransactionApproved("", 1, "", resultXDR, "").operationResults mustEqual opResults
    }
  }

}
