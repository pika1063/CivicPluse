
import { useState, useEffect } from "react";
import "./App.css";

const API = "http://localhost:8000/api/complaints";

function App() {
  const [page, setPage] = useState("Dashboard");
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [connected, setConnected] = useState(false);

  const [form, setForm] = useState({
    name: "",
    category: "Water",
    title: "",
    description: ""
  });

  const loadComplaints = async () => {
    try {
      const response = await fetch(API);
      if (!response.ok) throw new Error("Backend unavailable");

      const data = await response.json();
      setComplaints(data);
      setConnected(true);
    } catch (error) {
      console.error(error);
      setConnected(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadComplaints();
  }, []);

  const submitComplaint = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(API, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams(form)
      });

      if (!response.ok) throw new Error("Submission failed");

      await loadComplaints();

      setForm({
        name: "",
        category: "Water",
        title: "",
        description: ""
      });

      setPage("My Complaints");
      alert("Complaint submitted successfully!");
    } catch (error) {
      alert("Backend connection failed. Please check Java server.");
    }
  };

  const updateStatus = async (id, status) => {
    try {
      const response = await fetch(`${API}/status`, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({ id, status })
      });

      if (!response.ok) throw new Error("Status update failed");

      await loadComplaints();
    } catch (error) {
      alert("Unable to update complaint status.");
    }
  };

  const pending = complaints.filter(c => c.status === "Pending").length;
  const progress = complaints.filter(c => c.status === "In Progress").length;
  const resolved = complaints.filter(c => c.status === "Resolved").length;

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-icon">✦</span>
          <div>
            <h2>CivicPulse</h2>
            <small>Campus Connect</small>
          </div>
        </div>

        <p className="menu-label">WORKSPACE</p>

        {["Dashboard", "Submit Complaint", "My Complaints", "Admin Panel"].map(item => (
          <button
            key={item}
            className={`nav-item ${page === item ? "active" : ""}`}
            onClick={() => setPage(item)}
          >
            <span>{{
              "Dashboard": "⌂",
              "Submit Complaint": "+",
              "My Complaints": "▤",
              "Admin Panel": "⚙"
            }[item]}</span>
            {item}
          </button>
        ))}

        <div className="sidebar-bottom">
          <div className="profile">
            <div className="avatar">A</div>
            <div>
              <strong>Anshika</strong>
              <small>Student Account</small>
            </div>
          </div>
        </div>
      </aside>

      <main className="main">
        <header className="topbar">
          <div>
            <span className="breadcrumb">Workspace / </span>
            <strong>{page}</strong>
          </div>

          <div className="top-right">
            <span className={`status-dot ${connected ? "" : "offline"}`}></span>
            {connected ? "Backend Connected" : "Backend Offline"}
            <div className="avatar">A</div>
          </div>
        </header>

        <div className="content">

          {page === "Dashboard" && (
            <>
              <div className="welcome">
                <div>
                  <span className="eyebrow">CAMPUS SERVICE PORTAL</span>
                  <h1>Good day, Anshika! 👋</h1>
                  <p>Raise concerns, track progress, and help make your campus better.</p>
                </div>

                <button className="primary-btn" onClick={() => setPage("Submit Complaint")}>
                  + Submit a complaint
                </button>
              </div>

              <div className="stats">
                <div className="stat-card">
                  <div className="stat-top">
                    <span>Total Complaints</span>
                    <span className="stat-icon purple">▤</span>
                  </div>
                  <h2>{complaints.length}</h2>
                  <small>All submitted complaints</small>
                </div>

                <div className="stat-card">
                  <div className="stat-top">
                    <span>Pending</span>
                    <span className="stat-icon orange">◷</span>
                  </div>
                  <h2>{pending}</h2>
                  <small>Awaiting action</small>
                </div>

                <div className="stat-card">
                  <div className="stat-top">
                    <span>In Progress</span>
                    <span className="stat-icon blue">↻</span>
                  </div>
                  <h2>{progress}</h2>
                  <small>Currently being handled</small>
                </div>

                <div className="stat-card">
                  <div className="stat-top">
                    <span>Resolved</span>
                    <span className="stat-icon green">✓</span>
                  </div>
                  <h2>{resolved}</h2>
                  <small>Successfully completed</small>
                </div>
              </div>

              <section className="panel">
                <div className="panel-heading">
                  <div>
                    <h3>Recent Complaints</h3>
                    <p>Latest complaints submitted on campus</p>
                  </div>
                  <button className="text-btn" onClick={() => setPage("My Complaints")}>
                    View all →
                  </button>
                </div>

                {loading ? (
                  <div className="empty">Loading complaints...</div>
                ) : (
                  <ComplaintTable complaints={complaints.slice(0, 5)} />
                )}
              </section>

              <div className="info-banner">
                <span>💡</span>
                <div>
                  <strong>Your voice matters!</strong>
                  <p>Report campus issues so the administration can take timely action.</p>
                </div>
              </div>
            </>
          )}

          {page === "Submit Complaint" && (
            <section className="panel form-panel">
              <div className="panel-heading">
                <div>
                  <h3>Submit a Complaint</h3>
                  <p>Tell us about an issue you are facing on campus.</p>
                </div>
                <span className="form-symbol">✎</span>
              </div>

              <form onSubmit={submitComplaint}>
                <label>Your Name</label>
                <input
                  required
                  placeholder="Enter your full name"
                  value={form.name}
                  onChange={e => setForm({ ...form, name: e.target.value })}
                />

                <label>Complaint Category</label>
                <select
                  value={form.category}
                  onChange={e => setForm({ ...form, category: e.target.value })}
                >
                  <option>Water</option>
                  <option>Electricity</option>
                  <option>Cleanliness</option>
                  <option>Internet / Wi-Fi</option>
                  <option>Classroom</option>
                  <option>Hostel</option>
                  <option>Other</option>
                </select>

                <label>Complaint Title</label>
                <input
                  required
                  placeholder="Briefly describe the issue"
                  value={form.title}
                  onChange={e => setForm({ ...form, title: e.target.value })}
                />

                <label>Description</label>
                <textarea
                  required
                  rows="5"
                  placeholder="Provide more details about the problem..."
                  value={form.description}
                  onChange={e => setForm({ ...form, description: e.target.value })}
                />

                <button className="primary-btn" type="submit">
                  Submit Complaint →
                </button>
              </form>
            </section>
          )}

          {page === "My Complaints" && (
            <section className="panel">
              <div className="panel-heading">
                <div>
                  <h3>My Complaints</h3>
                  <p>Track the status of submitted complaints.</p>
                </div>

                <button className="primary-btn" onClick={() => setPage("Submit Complaint")}>
                  + New Complaint
                </button>
              </div>

              {loading ? (
                <div className="empty">Loading complaints...</div>
              ) : (
                <ComplaintTable complaints={complaints} />
              )}
            </section>
          )}

          {page === "Admin Panel" && (
            <section className="panel">
              <div className="panel-heading">
                <div>
                  <h3>Admin Management</h3>
                  <p>Review complaints and update their status.</p>
                </div>
                <span className="admin-tag">ADMIN VIEW</span>
              </div>

              {loading ? (
                <div className="empty">Loading complaints...</div>
              ) : (
                <ComplaintTable
                  complaints={complaints}
                  admin
                  updateStatus={updateStatus}
                />
              )}
            </section>
          )}

        </div>
      </main>
    </div>
  );
}

function ComplaintTable({ complaints, admin = false, updateStatus }) {
  if (complaints.length === 0) {
    return (
      <div className="empty">
        📭
        <h3>No complaints yet</h3>
        <p>Submitted complaints will appear here.</p>
      </div>
    );
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>COMPLAINT</th>
            <th>CATEGORY</th>
            <th>DATE</th>
            <th>STATUS</th>
          </tr>
        </thead>

        <tbody>
          {complaints.map(c => (
            <tr key={c.id}>
              <td>
                <strong>{c.title}</strong>
                <small className="subtext">{c.name}</small>
              </td>

              <td>{c.category}</td>
              <td>{c.date}</td>

              <td>
                {admin ? (
                  <select
                    className="status-select"
                    value={c.status}
                    onChange={e => updateStatus(c.id, e.target.value)}
                  >
                    <option>Pending</option>
                    <option>In Progress</option>
                    <option>Resolved</option>
                  </select>
                ) : (
                  <span className={`status ${c.status.toLowerCase().replace(" ", "-")}`}>
                    {c.status}
                  </span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
