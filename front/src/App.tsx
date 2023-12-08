import { useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./style/App.css";
import Homepage from "./components/Homepage";

function App() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <Homepage />
    </div>
  );
}

export default App;
