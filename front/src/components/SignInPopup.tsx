import * as React from "react";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";

export default function AlertDialog({ message }: { message: string }) {
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <React.Fragment>
      <button className="review-button" onClick={handleClickOpen}>
        Submit
      </button>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
        sx={{
          "& .MuiDialog-paper": {
            width: "40%",
            height: "20%",
            maxWidth: "none",
          },
        }}
      >
        <DialogContent>
          <DialogContentText
            id="alert-dialog-description"
            sx={{
              display: "flex",
              textAlign: "center",
              justifyContent: "center",
              fontSize: "1.3rem", // Adjust the font size as needed
              color: "black",
            }}
          >
            {message}
          </DialogContentText>
        </DialogContent>
      </Dialog>
    </React.Fragment>
  );
}
