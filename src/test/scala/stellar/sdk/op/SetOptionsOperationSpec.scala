package stellar.sdk.op

import org.json4s.NoTypeHints
import org.json4s.native.JsonMethods.parse
import org.json4s.native.Serialization
import org.scalacheck.Arbitrary
import org.specs2.mutable.Specification
import stellar.sdk.{AccountSigner, ArbitraryInput, DomainMatchers}

class SetOptionsOperationSpec extends Specification with ArbitraryInput with DomainMatchers with JsonSnippets {

  implicit val arb: Arbitrary[Transacted[SetOptionsOperation]] = Arbitrary(genTransacted(genSetOptionsOperation))
  implicit val formats = Serialization.formats(NoTypeHints) + TransactedOperationDeserializer

  "set options operation" should {
    "serde via xdr" >> prop { actual: SetOptionsOperation =>
      Operation.fromXDR(actual.toXDR) must beSuccessfulTry.like {
        case expected: SetOptionsOperation => expected must beEquivalentTo(actual)
      }
    }

    "parse from json" >> prop { op: Transacted[SetOptionsOperation] =>
      val doc =
        s"""
           |{
           |  "_links": {
           |    "self": {"href": "https://horizon-testnet.stellar.org/operations/10157597659137"},
           |    "transaction": {"href": "https://horizon-testnet.stellar.org/transactions/17a670bc424ff5ce3b386dbfaae9990b66a2a37b4fbe51547e8794962a3f9e6a"},
           |    "effects": {"href": "https://horizon-testnet.stellar.org/operations/10157597659137/effects"},
           |    "succeeds": {"href": "https://horizon-testnet.stellar.org/effects?order=desc\u0026cursor=10157597659137"},
           |    "precedes": {"href": "https://horizon-testnet.stellar.org/effects?order=asc\u0026cursor=10157597659137"}
           |  },
           |  "id": "${op.id}",
           |  "paging_token": "10157597659137",
           |  "source_account": "${op.sourceAccount.accountId}",
           |  "created_at": "${formatter.format(op.createdAt)}",
           |  "transaction_hash": "${op.txnHash}",
           |  ${opt("inflation_dest", op.operation.inflationDestination.map(_.accountId))}
           |  ${opt("home_domain", op.operation.homeDomain)}
           |  ${opt("master_key_weight", op.operation.masterKeyWeight)}
           |  ${
          opt("signer_key", op.operation.signer.flatMap {
            case AccountSigner(accn, _) => Some(accn.accountId)
            case _ => None
          })
        }
           |  ${
          opt("signer_weight", op.operation.signer.flatMap {
            case AccountSigner(_, w) => Some(w)
            case _ => None
          })
        }
           |  ${opt("set_flags", op.operation.setFlags.map(_.map(_.i)))}
           |  ${opt("set_flags_s", op.operation.setFlags.map(_.map(_.s)))}
           |  ${opt("clear_flags", op.operation.clearFlags.map(_.map(_.i)))}
           |  ${opt("clear_flags_s", op.operation.clearFlags.map(_.map(_.s)))}
           |  ${opt("low_threshold", op.operation.lowThreshold)}
           |  ${opt("med_threshold", op.operation.mediumThreshold)}
           |  ${opt("high_threshold", op.operation.highThreshold)}
           |  "type": "set_options",
           |  "type_i": 5,
           |}
         """.stripMargin

      parse(doc).extract[Transacted[SetOptionsOperation]] mustEqual op

    }
  }
}
