package example.users
import example.users.Users.FirstName
import example.users.Users.LastName
import example.users.Users.NewUser
import example.users.Users.User
import example.users.Users.UserId
import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec
import io.circe.generic.semiauto.deriveCodec

private object Json {
  case class UserCreated(userId: UserId)

  implicit private val userIdCodec: Codec[UserId] = deriveUnwrappedCodec
  private implicit val firstNameCodec: Codec[FirstName] = deriveUnwrappedCodec
  private implicit val lastNameCodec: Codec[LastName] = deriveUnwrappedCodec

  implicit val newUserCodec: Codec[NewUser] = deriveCodec
  implicit val userCreatedCodec: Codec[UserCreated] = deriveCodec
  implicit val userCodec: Codec[User] = deriveCodec
}
