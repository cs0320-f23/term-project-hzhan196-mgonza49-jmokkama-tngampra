import { ReactNode } from "react";
export function Icons(image: string, name: string): ReactNode {
  return (
    <button>
      <div className="circular-container">
        <img src={image} alt="country" className="circular-image" />
      </div>
      <div className="description">{name}</div>
    </button>
  );
}
