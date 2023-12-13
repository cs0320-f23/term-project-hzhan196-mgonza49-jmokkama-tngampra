import {
  getAuth,
  signInWithPopup,
  GoogleAuthProvider,
  UserCredential,
  signOut,
  onAuthStateChanged,
} from "firebase/auth";
import app from "./firebaseInit";

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
          console.log("null !!!");
          reject("Error: No email provided");
        } else if (!user.email.endsWith("@brown.edu")) {
          console.log("not a brown email");
          reject("Error: You do not have access to this page");
        } else {
          console.log("success");
          resolve("success");
        }
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

export function loginStatus(): Promise<string> {
  return new Promise((resolve) => {
    const auth = getAuth(app);
    onAuthStateChanged(auth, (user) => {
      if (user != null) {
        console.log("user, " + user.email);
      }
      if (user) {
        resolve("Sign Out");
      } else {
        resolve("Sign In");
      }
    });
  });
}
