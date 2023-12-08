interface UserProps {}

export default function Visual({}: UserProps) {
  return (
    <div>
      <header className="header">
        Brown Study Abroad
        <button className="button">Home</button>
        <button className="button">Login</button>
        <div className="search-container">
          <input type="text" id="searchInput" />
          {/* <button onclick="toggleSearch()"/> */}
        </div>
      </header>
      <body>
        <div className="main">body</div>
      </body>
      <script>function openSearch() {}</script>
    </div>
  );
}
