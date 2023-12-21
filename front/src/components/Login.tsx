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
        const user = result.user;
        if (user.email == null) {
          console.log("null !!!");
          reject("Error: No email provided");
        } else if (!user.email.endsWith("@brown.edu")) {
          console.log("not a brown email");
          logout();
          reject("Error: You do not have access to this page");
        } else {
          console.log("success");
          window.location.reload();
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
        window.location.reload();
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
      if (user) {
        resolve("Sign Out");
      } else {
        resolve("Sign In");
      }
    });
  });
}

export function profilePhoto(): Promise<string | null> {
  return new Promise((resolve) => {
    const auth = getAuth(app);
    onAuthStateChanged(auth, (user) => {
      if (user) {
        resolve(user.photoURL);
      } else {
        resolve("error");
      }
    });
  });
}

export function profileName(): Promise<string> {
  return new Promise((resolve) => {
    const auth = getAuth(app);
    onAuthStateChanged(auth, (user) => {
      if (user && user.displayName) {
        resolve(user.displayName);
      } else {
        resolve("error");
      }
    });
  });
}

export function profileEmail(): Promise<string> {
  return new Promise((resolve) => {
    const auth = getAuth(app);
    onAuthStateChanged(auth, (user) => {
      if (user && user.email) {
        resolve(user.email);
      } else {
        resolve("error");
      }
    });
  });
}
