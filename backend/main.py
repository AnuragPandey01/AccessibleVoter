from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel
from typing import List, Tuple
from sqlalchemy import create_engine, Column, String, Integer, Float, ARRAY, MetaData
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# Define the database URL (adjust with your PostgreSQL credentials)
DATABASE_URL = "postgresql://postgres:NW69XRPG@localhost/unabled_voting"

# Define a passcode (this should be stored securely in a real application)
VALID_PASSCODE = "aditya6969"  # Replace this with your actual passcode

# Create the database engine and session
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
metadata = MetaData()
Base = declarative_base()

# Updated Database Model
class FaceData(Base):
    __tablename__ = "face_data"
    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String)
    aadhaar = Column(String, unique=True, index=True)
    phone = Column(String)
    bounds = Column(ARRAY(Float))  # To store bounding box data as a list of four floats
    face_points_x = Column(ARRAY(Float))  # List of x-coordinates for face mesh points
    face_points_y = Column(ARRAY(Float))  # List of y-coordinates for face mesh points
    triangle_indices = Column(ARRAY(Integer), nullable=True)  # List of triangle indices as integers
    voted_to_party_code = Column(String, nullable=True)


class PoliticalParty(Base):
    __tablename__ = "political_parties"
    id = Column(Integer, primary_key=True, index=True)
    party_name = Column(String, unique=True, index=True)
    party_code = Column(String, unique=True, index=True)
    leader = Column(String)

class Vote(Base):
    __tablename__ = 'votes'

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False)  # Reference to the user's ID
    party_code = Column(String, nullable=False)
    party_name = Column(String, nullable=False)

# Create the table in the database
Base.metadata.create_all(bind=engine)




# Define the Pydantic Model for incoming request
class FaceMeshPoint(BaseModel):
    index: int
    position: Tuple[float, float]

class FaceMeshData(BaseModel):
    full_name: str
    aadhaar: str
    phone: str
    bounds: Tuple[float, float, float, float]  # x_min, y_min, x_max, y_max
    face_points: List[FaceMeshPoint]
    triangle_indices: List[Tuple[int, int, int]]  # List of triangles represented by indices

class PoliticalPartyResponse(BaseModel):
    id: int
    party_name: str
    party_code: str
    leader: str


class VoteRequest(BaseModel):
    user_id: int
    party_code: str
    party_name: str

class VoteResponse(BaseModel):
    user_id: int
    aadhaar_card_number: str  # Ensure the field name matches this
    phone_number: str          # Ensure the field name matches this
    face_details: dict
    party_code: str
    party_name: str


class PasscodeRequest(BaseModel):
    passcode: str





app = FastAPI()


# Endpoint to handle the incoming face mesh data
@app.post("/save-user-data/")
def save_face_data(data: FaceMeshData):
    db = SessionLocal()
    try:
        # Separate face points into x and y coordinate lists
        face_points_x = [point.position[0] for point in data.face_points]
        face_points_y = [point.position[1] for point in data.face_points]
        # Flatten triangle indices for storage
        triangle_indices_list = [index for triangle in data.triangle_indices for index in triangle]

        # Create a new record for the face data
        face_record = FaceData(
            full_name=data.full_name,
            aadhaar=data.aadhaar,
            phone=data.phone,
            bounds=list(data.bounds),
            face_points_x=face_points_x,
            face_points_y=face_points_y,
            triangle_indices=triangle_indices_list,
        )

        db.add(face_record)
        db.commit()
        db.refresh(face_record)  # Refresh the record to get the generated ID

        # Return the ID of the newly created record
        return {"status": "success", "data": f"Face mesh data saved successfully!", "id": face_record.id}
    except Exception as e:
        db.rollback()
        return {"status": "error", "data": str(e)}
    finally:
        db.close()


@app.get("/political-parties/", response_model=List[PoliticalPartyResponse])
def get_political_parties():
    db = SessionLocal()
    try:
        parties = db.query(PoliticalParty).all()
        if not parties:
            raise HTTPException(status_code=404, detail="No political parties found")

        return parties
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        db.close()



@app.post("/votes/", response_model=VoteResponse)
def create_vote(vote_request: VoteRequest):
    db = SessionLocal()
    try:
        # Fetch user details based on user_id
        face_data = db.query(FaceData).filter(FaceData.id == vote_request.user_id).first()
        if not face_data:
            raise HTTPException(status_code=404, detail="User not found")

        # Create a new vote entry
        new_vote = Vote(user_id=vote_request.user_id, party_code=vote_request.party_code, party_name=vote_request.party_name)
        db.add(new_vote)
        
        # Update the FaceData entry with the voted party code
        face_data.voted_to_party_code = vote_request.party_code
        
        db.commit()
        db.refresh(new_vote)

        # Return user and vote information along with face details
        return {
            "user_id": face_data.id,
            "aadhaar_card_number": face_data.aadhaar,
            "phone_number": face_data.phone,
            "face_details": {
                "bounds": face_data.bounds,
                "face_points_x": face_data.face_points_x,
                "face_points_y": face_data.face_points_y,
                "triangle_indices": face_data.triangle_indices,
            },
            "party_code": new_vote.party_code,
            "party_name": new_vote.party_name,
        }
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        db.close()


@app.get("/user/")
def get_user_data(request: PasscodeRequest, user_id: int = Query(...)):
    # Verify the provided passcode
    if request.passcode != VALID_PASSCODE:
        raise HTTPException(status_code=403, detail="Invalid passcode")

    db = SessionLocal()
    try:
        # Retrieve the face data for the given user ID
        face_data = db.query(FaceData).filter(FaceData.id == user_id).first()
        
        if not face_data:
            raise HTTPException(status_code=404, detail="User not found")
        
        # Return the user's face data
        return {
            "user_id": face_data.id,
            "full_name": face_data.full_name,
            "aadhaar_card_number": face_data.aadhaar,
            "phone_number": face_data.phone,
            "face_details": {
                "bounds": face_data.bounds,
                "face_points_x": face_data.face_points_x,
                "face_points_y": face_data.face_points_y,
                "triangle_indices": face_data.triangle_indices,
            },
            "voted_to_party_code": face_data.voted_to_party_code,
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        db.close()