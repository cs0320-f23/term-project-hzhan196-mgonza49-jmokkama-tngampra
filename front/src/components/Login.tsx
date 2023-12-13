import {
  getAuth,
  signInWithPopup,
  GoogleAuthProvider,
  UserCredential,
} from "firebase/auth";
import app from "./firebaseInit";

export function Login(): Promise<string> {
  return new Promise((resolve, reject) => {
    const auth = getAuth(app);
    console.log("auth " + auth);
    const provider = new GoogleAuthProvider();

    signInWithPopup(auth, provider)
      .then((result: UserCredential) => {
        const credential = GoogleAuthProvider.credentialFromResult(result);
        console.log("got into fetch");
        // const token = credential.accessToken;
        const user = result.user;
        console.log("user email " + user.email);
        if (user.email == null) {
          reject("Error: No email provided");
        } else if (!user.email.endsWith("@brown.edu")) {
          reject("Error: You do not have access to this page");
        }
        resolve("success");
      })
      .catch((error) => {
        console.log("error " + error);
        // console.error(error.message);
        reject("Error: " + error);
      });
  });
}
