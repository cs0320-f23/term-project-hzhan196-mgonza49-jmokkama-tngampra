import {
  getAuth,
  signInWithPopup,
  GoogleAuthProvider,
  UserCredential,
  signOut,
} from "firebase/auth";
import app from "./firebaseInit";

const loggedIn = false;

export function login(): Promise<string> {
  return new Promise((resolve, reject) => {
    const auth = getAuth(app);
    console.log("auth " + auth);
    const provider = new GoogleAuthProvider();

    signInWithPopup(auth, provider)
      .then((result: UserCredential) => {
        // const credential = GoogleAuthProvider.credentialFromResult(result);
        // const token = credential.accessToken;
        const user = result.user;
        if (user.email == null) {
          reject("Error: No email provided");
        } else if (!user.email.endsWith("@brown.edu")) {
          reject("Error: You do not have access to this page");
        }
        resolve("success");
      })
      .catch((error) => {
        reject("Error: " + error);
      });
  });
}

export function logout(): Promise<String> {
  return new Promise((resolve, reject) => {
    const auth = getAuth(app);
    signOut(auth)
      .then(() => {
        resolve("success");
      })
      .catch((error) => {
        reject("Error: " + error);
      });
  });
}
